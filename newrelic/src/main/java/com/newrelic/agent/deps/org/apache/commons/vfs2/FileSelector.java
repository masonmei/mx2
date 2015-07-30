package com.newrelic.agent.deps.org.apache.commons.vfs2;


/**
 * This interface is used to select files when traversing a file hierarchy.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 * @see Selectors
 */
public interface FileSelector
{
    /**
     * Determines if a file or folder should be selected.  This method is
     * called in depthwise order (that is, it is called for the children
     * of a folder before it is called for the folder itself).
     *
     * @param fileInfo the file or folder to select.
     * @return true if the file should be selected.
     * @throws Exception if an error occurs.
     */
    boolean includeFile(FileSelectInfo fileInfo)
            throws Exception;

    /**
     * Determines whether a folder should be traversed.  If this method returns
     * true, {@link #includeFile} is called for each of the children of
     * the folder, and each of the child folders is recursively traversed.
     * <p/>
     * <p>This method is called on a folder before {@link #includeFile}
     * is called.
     *
     * @param fileInfo the file or folder to select.
     * @return true if the folder should be traversed.
     * @throws Exception if an error occurs.
     */
    boolean traverseDescendents(FileSelectInfo fileInfo)
            throws Exception;
}
