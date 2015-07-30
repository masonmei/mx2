package com.newrelic.agent.deps.org.apache.commons.vfs2;


/**
 * An enumerated type to deal with the various cache strategies.
 *
 * @author <a href="http://commons.apache.org/vfs/team-list.html">Commons VFS team</a>
 */
public enum CacheStrategy
{
    /**
     * Deal with cached data manually. Call {@link FileObject#refresh()} to refresh the object data.
     */
    MANUAL("manual"),

    /**
     * Refresh the data every time you request a file from {@link FileSystemManager#resolveFile}.
     */
    ON_RESOLVE("onresolve"),

    /**
     * Refresh the data every time you call a method on the fileObject.
     * You'll use this only if you really need the latest info as this setting is a major performance
     * loss.
     */
    ON_CALL("oncall");

    /**
     * Cache strategy name
     */
    private final String realName;

    private CacheStrategy(final String name)
    {
        this.realName = name;
    }

    /**
     * Returns the name of the scope.
     * @return the name of the scope.
     */
    @Override
    public String toString()
    {
        return realName;
    }

    /**
     * Returns the name of the scope.
     * @return the name of the scope.
     */
    public String getName()
    {
        return realName;
    }
}

