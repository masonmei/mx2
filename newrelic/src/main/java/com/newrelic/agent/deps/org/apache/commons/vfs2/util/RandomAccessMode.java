package com.newrelic.agent.deps.org.apache.commons.vfs2.util;


/**
 * An enumerated type representing the modes of a random access content.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public enum RandomAccessMode
{
    /**
     * read.
     */
    READ(true, false),

    /**
     * read/write.
     */
    READWRITE(true, true);


    private final boolean read;
    private final boolean write;

    private RandomAccessMode(final boolean read, final boolean write)
    {
        this.read = read;
        this.write = write;
    }

    public boolean requestRead()
    {
        return read;
    }

    public boolean requestWrite()
    {
        return write;
    }

    /**
     * @return The mode String.
     * @since 2.0
     * */
    public String getModeString()
    {
        if (requestRead())
        {
            if (requestWrite())
            {
                return "rw"; // NON-NLS
            }
            else
            {
                return "r"; // NON-NLS
            }
        }
        else if (requestWrite())
        {
            return "w"; // NON-NLS
        }

        return "";
    }
}

