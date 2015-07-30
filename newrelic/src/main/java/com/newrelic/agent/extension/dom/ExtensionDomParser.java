// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.extension.dom;

import org.xml.sax.SAXParseException;
import java.lang.reflect.Method;
import javax.xml.transform.TransformerFactoryConfigurationError;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import javax.xml.validation.SchemaFactory;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import javax.xml.transform.Transformer;
import com.newrelic.agent.extension.jaxb.Unmarshaller;
import javax.xml.validation.Validator;
import javax.xml.validation.Schema;
import javax.xml.transform.Result;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import javax.xml.transform.TransformerFactory;
import com.newrelic.agent.extension.jaxb.UnmarshallerFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.File;
import java.text.MessageFormat;
import org.w3c.dom.Document;
import java.util.logging.Level;
import com.newrelic.agent.Agent;
import com.newrelic.agent.extension.beans.Extension;
import java.util.List;
import org.xml.sax.ErrorHandler;

public class ExtensionDomParser
{
    private static final ErrorHandler LOGGING_ERROR_HANDLER;
    private static final ErrorHandler IGNORE_ERROR_HANDLER;
    private static final String NAMESPACE = "https://newrelic.com/docs/java/xsd/v1.0";
    
    public static Extension readStringGatherExceptions(final String xml, final List<Exception> exceptions) {
        if (xml == null || xml.length() == 0) {
            Agent.LOG.log(Level.FINE, "The input xml string is empty.");
            return null;
        }
        try {
            final Document doc = getDocument(xml, false);
            return parseDocument(doc);
        }
        catch (Exception e) {
            exceptions.add(e);
            return null;
        }
    }
    
    public static Extension readStringCatchException(final String xml) {
        if (xml == null || xml.length() == 0) {
            Agent.LOG.log(Level.FINE, "The input xml string is empty.");
            return null;
        }
        try {
            final Document doc = getDocument(xml, false);
            final Extension ext = parseDocument(doc);
            return ext;
        }
        catch (Exception e) {
            Agent.LOG.log(Level.WARNING, MessageFormat.format("Failed to read extension {0}. Skipping the extension. Reason: {1}", xml, e.getMessage()));
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Reason For Failure: " + e.getMessage(), e);
            }
            return null;
        }
    }
    
    public static Extension readFileCatchException(final File file) {
        try {
            return readFile(file);
        }
        catch (Exception e) {
            Agent.LOG.log(Level.WARNING, MessageFormat.format("Failed to read extension {0}. Skipping the extension. Reason: {1}", file.getName(), e.getMessage()));
            if (Agent.LOG.isFinerEnabled()) {
                Agent.LOG.log(Level.FINER, "Reason For Failure: " + e.getMessage(), e);
            }
            return null;
        }
    }
    
    public static Extension readFile(final File file) throws SAXException, IOException, ParserConfigurationException, JAXBException, NoSuchMethodException, SecurityException {
        return parseDocument(getDocument(file));
    }
    
    public static Extension readFile(final InputStream inputStream) throws SAXException, IOException, ParserConfigurationException, JAXBException, NoSuchMethodException, SecurityException {
        return parseDocument(getDocument(new InputSource(inputStream), true));
    }
    
    public static Extension parseDocument(Document doc) throws SAXException, IOException, ParserConfigurationException, NoSuchMethodException, SecurityException {
        trimTextNodeWhitespace(doc.getDocumentElement());
        doc = fixNamespace(doc);
        final Schema schema = getSchema();
        final Validator validator = schema.newValidator();
        validator.validate(new DOMSource(doc));
        try {
            final Unmarshaller<Extension> unmarshaller = UnmarshallerFactory.create(Extension.class);
            return unmarshaller.unmarshall(doc);
        }
        catch (Exception ex) {
            try {
                final Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.setOutputProperty("indent", "yes");
                final StreamResult result = new StreamResult(new StringWriter());
                final DOMSource source = new DOMSource(doc);
                transformer.transform(source, result);
                final String xmlString = result.getWriter().toString();
                System.out.println(xmlString);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            throw new IOException(ex);
        }
    }
    
    private static Document getDocument(final String pXml, final boolean setSchema) throws SAXException, IOException, ParserConfigurationException, NoSuchMethodException, SecurityException {
        ByteArrayInputStream baos = null;
        try {
            baos = new ByteArrayInputStream(pXml.getBytes());
            final Document document = getDocument(new InputSource(baos), setSchema);
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (IOException ex) {}
            }
            return document;
        }
        finally {
            if (baos != null) {
                try {
                    baos.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    private static Document getDocument(final File file) throws SAXException, IOException, ParserConfigurationException, NoSuchMethodException, SecurityException {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            final Document document = getDocument(new InputSource(fis), true);
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException ex) {}
            }
            return document;
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException ex2) {}
            }
        }
    }
    
    private static Schema getSchema() throws IOException, SAXException, ParserConfigurationException, NoSuchMethodException, SecurityException {
        final URL schemaFile = Agent.getClassLoader().getResource("META-INF/extensions/extension.xsd");
        if (schemaFile == null) {
            throw new IOException("Unable to load the extension schema");
        }
        Agent.LOG.finest("Loading extension schema from " + schemaFile);
        final SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        final DocumentBuilderFactory factory = getDocumentBuilderFactory();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(ExtensionDomParser.LOGGING_ERROR_HANDLER);
        final Document schemaDoc = builder.parse(schemaFile.openStream());
        return schemaFactory.newSchema(new DOMSource(schemaDoc));
    }
    
    private static Document getDocument(final InputSource inputSource, final boolean setSchema) throws SAXException, IOException, ParserConfigurationException, NoSuchMethodException, SecurityException {
        final DocumentBuilderFactory factory = getDocumentBuilderFactory();
        if (setSchema) {
            final Schema schema = getSchema();
            factory.setSchema(schema);
        }
        final DocumentBuilder builder = factory.newDocumentBuilder();
        builder.setErrorHandler(ExtensionDomParser.IGNORE_ERROR_HANDLER);
        return builder.parse(inputSource);
    }
    
    private static DocumentBuilderFactory getDocumentBuilderFactory() throws ParserConfigurationException, NoSuchMethodException, SecurityException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            setupDocumentFactory(factory);
        }
        catch (AbstractMethodError e) {
            return getAndSetupDocumentBuilderComSunFactory();
        }
        return factory;
    }
    
    private static void setupDocumentFactory(final DocumentBuilderFactory factory) throws ParserConfigurationException {
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(false);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setValidating(false);
        factory.setIgnoringElementContentWhitespace(true);
    }
    
    private static DocumentBuilderFactory getAndSetupDocumentBuilderComSunFactory() throws NoSuchMethodError {
        try {
            final Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");
            final DocumentBuilderFactory factory = (DocumentBuilderFactory)clazz.newInstance();
            setupDocumentFactory(factory);
            return factory;
        }
        catch (Throwable e) {
            Agent.LOG.info("Your application has loaded a Java 1.4 or below implementation of the class DocumentBuilderFactory. Please upgrade to a 1.5 version if you want to use Java agent XML instrumentation.");
            throw new NoSuchMethodError("The method setFeature can not be called.");
        }
    }
    
    public static void trimTextNodeWhitespace(final Node e) {
        final NodeList children = e.getChildNodes();
        for (int i = 0; i < children.getLength(); ++i) {
            final Node child = children.item(i);
            if (child instanceof Text) {
                final Text text = (Text)child;
                text.setData(text.getData().trim());
            }
            trimTextNodeWhitespace(child);
        }
    }
    
    private static Document fixNamespace(final Document doc) {
        try {
            final Transformer transformer = getTransformerFactory().newTransformer();
            final StreamResult result = new StreamResult(new StringWriter());
            final DOMSource source = new DOMSource(doc);
            transformer.transform(source, result);
            String xmlString = result.getWriter().toString();
            xmlString = xmlString.replace("xmlns:urn=\"newrelic-extension\"", "xmlns:urn=\"https://newrelic.com/docs/java/xsd/v1.0\"");
            return getDocument(xmlString, true);
        }
        catch (Exception ex) {
            return doc;
        }
    }
    
    private static TransformerFactory getTransformerFactory() throws TransformerFactoryConfigurationError {
        try {
            return TransformerFactory.newInstance();
        }
        catch (TransformerFactoryConfigurationError ex) {
            try {
                final Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
                final Method method = clazz.getMethod("newTransformerFactoryNoServiceLoader", (Class<?>[])new Class[0]);
                return (TransformerFactory)method.invoke(null, new Object[0]);
            }
            catch (Exception e) {
                throw ex;
            }
        }
    }
    
    static {
        LOGGING_ERROR_HANDLER = new ErrorHandler() {
            public void warning(final SAXParseException exception) throws SAXException {
                Agent.LOG.log(Level.FINEST, exception.toString(), exception);
            }
            
            public void fatalError(final SAXParseException exception) throws SAXException {
                Agent.LOG.log(Level.FINER, exception.toString(), exception);
            }
            
            public void error(final SAXParseException exception) throws SAXException {
                Agent.LOG.log(Level.FINEST, exception.toString(), exception);
            }
        };
        IGNORE_ERROR_HANDLER = new ErrorHandler() {
            public void warning(final SAXParseException exception) throws SAXException {
            }
            
            public void fatalError(final SAXParseException exception) throws SAXException {
            }
            
            public void error(final SAXParseException exception) throws SAXException {
            }
        };
    }
}
