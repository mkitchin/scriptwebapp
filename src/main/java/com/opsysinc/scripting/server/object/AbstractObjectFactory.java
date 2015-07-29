package com.opsysinc.scripting.server.object;

/**
 * Abstract object factory.
 *
 * @param <T> Type of object to create.
 * @author mkit
 */
public abstract class AbstractObjectFactory<T> implements ObjectFactory<T> {

    /**
     * Object class.
     */
    private final Class<T> objectClass;

    /**
     * Min args.
     */
    private final int minArgs;

    /**
     * Max args.
     */
    private final int maxArgs;

    /**
     * Basic ctor.
     *
     * @param objectClass Object class.
     * @param minArgs     Min args (-1 = unspecified).
     * @param maxArgs     Max args (-1 = unspecified).
     */
    protected AbstractObjectFactory(final Class<T> objectClass,
                                    final int minArgs, final int maxArgs) {

        this.objectClass = objectClass;
        this.minArgs = minArgs;
        this.maxArgs = maxArgs;
    }

    @Override
    public T create(final Object... args) throws Throwable {

        boolean isGood = true;

        if (args == null) {

            if (this.minArgs > 0) {

                isGood = false;
            }

        } else if ((this.minArgs > -1) && (args.length < this.minArgs)) {

            isGood = false;

        } else if ((this.maxArgs > -1) && (args.length > this.maxArgs)) {

            isGood = false;
        }

        if (!isGood) {

            this.throwMissingInput();
        }

        return this.createImpl(args);
    }

    /**
     * Implementation method, called after arg count check.
     *
     * @param args Arguments.
     * @return Created object.
     * @throws Throwable Any exception (caller handles).
     */
    protected abstract T createImpl(Object... args) throws Throwable;

    /**
     * Gets max args.
     *
     * @return Max args.
     */
    public int getMaxArgs() {
        return this.maxArgs;
    }

    /**
     * Gets min args.
     *
     * @return Min args.
     */
    public int getMinArgs() {
        return this.minArgs;
    }

    /**
     * Gets object class.
     *
     * @return Object class.
     */
    public Class<T> getObjectClass() {
        return this.objectClass;
    }

    /**
     * Throw on missing input.
     */
    protected void throwMissingInput() {

        throw new IllegalArgumentException("missing input (minargs="
                + this.minArgs + ", maxargs=" + this.maxArgs + ")");
    }
}
