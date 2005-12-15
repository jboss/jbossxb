package org.jboss.util.stream;

import java.io.IOException;

/**
 * Tag interface for MarshalledValues
 * @author <a href="mailto:clebert.suconic@jboss.com">Clebert Suconic</a>
 */
public interface IMarshalledValue {
    public Object get() throws IOException, ClassNotFoundException;
}
