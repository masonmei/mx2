// 
// Decompiled by Procyon v0.5.29
// 

package com.newrelic.agent.deps.org.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.newrelic.agent.deps.org.apache.commons.vfs2.FileType;
import java.util.Collection;
import java.util.Stack;
import com.newrelic.agent.deps.com.google.common.collect.AbstractIterator;
import java.util.Iterator;
import com.newrelic.agent.deps.org.apache.commons.vfs2.FileSystemException;
import com.newrelic.agent.deps.org.apache.commons.vfs2.FileObject;

public interface CommonsVfs2UrlType
{
    class Dir implements Vfs.Dir
    {
        private final FileObject file;
        
        public Dir(FileObject file) {
            this.file = file;
        }
        
        public String getPath() {
            try {
                return this.file.getURL().getPath();
            }
            catch (FileSystemException e) {
                throw new RuntimeException(e);
            }
        }
        
        public Iterable<Vfs.File> getFiles() {
            return new Iterable<Vfs.File>() {
                public Iterator<Vfs.File> iterator() {
                    return new FileAbstractIterator();
                }
            };
        }
        
        public void close() {
            try {
                this.file.close();
            }
            catch (FileSystemException ex) {}
        }
        
        private class FileAbstractIterator extends AbstractIterator<Vfs.File>
        {
            final Stack<FileObject> stack;
            
            private FileAbstractIterator() {
                this.stack = new Stack<FileObject>();
                this.listDir(Dir.this.file);
            }
            
            protected Vfs.File computeNext() {
                FileObject file;
                while (!this.stack.isEmpty()) {
                    file = this.stack.pop();
                    try {
                        if (!this.isDir(file)) {
                            return this.getFile(file);
                        }
                        this.listDir(file);
                    }
                    catch (FileSystemException e) {
                        throw new RuntimeException(e);
                    }
                }
                return this.endOfData();
            }
            
            private CommonsVfs2UrlType.File getFile(FileObject file) {
                return new CommonsVfs2UrlType.File(Dir.this.file, file);
            }
            
            private boolean listDir(FileObject file) {
                return this.stack.addAll(this.listFiles(file));
            }
            
            private boolean isDir(FileObject file) throws FileSystemException {
                return file.getType() == FileType.FOLDER;
            }
            
            protected List<FileObject> listFiles(FileObject file) {
                FileObject[] files;
                try {
                    files = file.getType().hasChildren() ? file.getChildren() : null;
                    return (files != null) ? Arrays.asList(files) : new ArrayList<FileObject>();
                }
                catch (FileSystemException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    class File implements Vfs.File
    {
        private final FileObject root;
        private final FileObject file;
        
        public File(FileObject root, FileObject file) {
            this.root = root;
            this.file = file;
        }
        
        public String getName() {
            return this.file.getName().getBaseName();
        }
        
        public String getRelativePath() {
            String filepath;
            filepath = this.file.getName().getPath().replace("\\", "/");
            if (filepath.startsWith(this.root.getName().getPath())) {
                return filepath.substring(this.root.getName().getPath().length() + 1);
            }
            return null;
        }
        
        public InputStream openInputStream() throws IOException {
            return this.file.getContent().getInputStream();
        }
    }
}
