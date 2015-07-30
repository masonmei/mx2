package com.newrelic.agent.deps.org.apache.commons.vfs2;

/**
 * An event fired when a file is changed.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public class FileChangeEvent
{
    /**
     * The file object
     */
    private final FileObject file;

    public FileChangeEvent(final FileObject file)
    {
        this.file = file;
    }

    /**
     * Returns the file that changed.
     * @return The FileObject that was changed.
     */
    public FileObject getFile()
    {
        return file;
    }
}
