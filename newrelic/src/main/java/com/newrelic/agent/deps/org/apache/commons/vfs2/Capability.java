package com.newrelic.agent.deps.org.apache.commons.vfs2;


/**
 * An enumerated type representing the capabilities of files and file systems.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public enum Capability
{
    /**
     * File content can be read.
     */
    READ_CONTENT,

    /**
     * File content can be written.
     */
    WRITE_CONTENT,

    /**
     * File content can be read in random mode.<br>
     */
    RANDOM_ACCESS_READ,

    /**
     * File content can be written in random mode.<br>
     */
    RANDOM_ACCESS_WRITE,

    /**
     * File content can be appended.
     */
    APPEND_CONTENT,

    /**
     * File attributes are supported.
     */
    ATTRIBUTES,

    /**
     * File last-modified time is supported.
     */
    LAST_MODIFIED,

    /**
     * File get last-modified time is supported.
     */
    GET_LAST_MODIFIED,

    /**
     * File set last-modified time is supported.
     */
    SET_LAST_MODIFIED_FILE,

    /**
     * folder set last-modified time is supported.
     */
    SET_LAST_MODIFIED_FOLDER,

    /**
     * File content signing is supported.
     */
    SIGNING,

    /**
     * Files can be created.
     */
    CREATE,

    /**
     * Files can be deleted.
     */
    DELETE,

    /**
     * Files can be renamed.
     */
    RENAME,

    /**
     * The file type can be determined.
     */
    GET_TYPE,

    /**
     * Children of files can be listed.
     */
    LIST_CHILDREN,

    /**
     * URI are supported.  Files without this capability use URI that do not
     * globally and uniquely identify the file.
     */
    URI,

    /**
     * File system attributes are supported.
     */
    FS_ATTRIBUTES,

    /**
     * Junctions are supported.
     */
    JUNCTIONS,

    /**
     * The set of attributes defined by the Jar manifest specification are
     * supported.  The attributes aren't necessarily stored in a manifest file.
     */
    MANIFEST_ATTRIBUTES,

    /**
     * The provider itself do not provide a filesystem. It simply resolves a full name
     * and dispatches the request back to the filesystemmanager.<br>
     * A provider with this capability cant tell much about the capabilities about the
     * finally used filesystem in advance.
     */
    DISPATCHER,

    /**
     * A compressed filesystem is a filesystem which use compression.
     */
    COMPRESS,

    /**
     * A virtual filesystem can be an archive like tar or zip.
     */
    VIRTUAL,

    /**
     * Provides directories which allows you to read its content through
     * {@link org.apache.commons.vfs2.FileContent#getInputStream()}.
     * @since 2.0
     */
    DIRECTORY_READ_CONTENT;
}