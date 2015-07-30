package com.newrelic.agent.deps.org.apache.commons.vfs2;


/**
 * An enumerated type that represents a file's type.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public enum FileType
{
    /**
     * A folder.  May contain other files, and have attributes, but does not
     * have any data content.
     */
    FOLDER("folder", true, false, true),

    /**
     * A regular file.  May have data content and attributes, but cannot
     * contain other files.
     */
    FILE("file", false, true, true),

    /**
     * A file or folder.  May have data content and attributes, and can
     * contain other files.
     */
    FILE_OR_FOLDER("fileOrFolder", true, true, true),

    /**
     * A file that does not exist.  May not have data content, attributes,
     * or contain other files.
     */
    IMAGINARY("imaginary", false, false, false);

    /** The name of the FileType */
    private final String name;

    /** true if the FileType can have children */
    private final boolean hasChildren;

    /** true if the FileType can have content */
    private final boolean hasContent;

    /** true if the FileType has attributes */
    private final boolean hasAttrs;

    private FileType(final String name,
                     final boolean hasChildren,
                     final boolean hasContent,
                     final boolean hasAttrs)
    {
        this.name = name;
        this.hasChildren = hasChildren;
        this.hasContent = hasContent;
        this.hasAttrs = hasAttrs;
    }

    /**
     * Returns the name of this type.
     * @return The name of this type.
     */
    @Override
    public String toString()
    {
        return name;
    }

    /**
     * Returns the name of this type.
     * @return The name of the type.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns true if files of this type may contain other files.
     * @return tru if files can contain other files.
     */
    public boolean hasChildren()
    {
        return hasChildren;
    }

    /**
     * Returns true if files of this type may have data content.
     * @return true if files can have content.
     */
    public boolean hasContent()
    {
        return hasContent;
    }

    /**
     * Returns true if files of this type may have attributes.
     * @return true if files can have attributes
     */
    public boolean hasAttributes()
    {
        return hasAttrs;
    }
}
