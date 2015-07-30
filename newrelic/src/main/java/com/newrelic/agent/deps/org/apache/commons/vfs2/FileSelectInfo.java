package com.newrelic.agent.deps.org.apache.commons.vfs2;

/**
 * Information about a file, that is used to select files during the
 * traversal of a hierarchy.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 * @todo Rename this interface, as it is used by both FileSelector and FileVisitor.
 */
public interface FileSelectInfo
{
    /**
     * Returns the base folder of the traversal.
     * @return FileObject representing the base folder.
     */
    FileObject getBaseFolder();

    /**
     * Returns the file (or folder) to be considered.
     * @return The FileObject.
     */
    FileObject getFile();

    /**
     * Returns the depth of the file relative to the base folder.
     * @return The depth of the file relative to the base folder.
     */
    int getDepth();
}

