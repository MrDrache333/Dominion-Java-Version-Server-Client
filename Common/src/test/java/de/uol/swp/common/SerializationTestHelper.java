package de.uol.swp.common;

import java.io.*;

/**
 * Hilfsklasse, um zu Testen ob ein Objekt serialisierbar ist
 * <p>
 * https://stackoverflow.com/questions/3840356/how-to-test-in-java-that-a-class-implements-serializable-correctly-not-just-is
 *
 * @author Keno S.
 * @since Sprint 3
 */
public class SerializationTestHelper {

    /**
     * Hilfsfunktion
     *
     * @param obj das Objekt
     * @param <T> Generics Parameter
     * @return baos.toByteArray()
     * @throws IOException die Exception
     * @author Keno S.
     * @since Sprint 4
     */
    public static <T extends Serializable> byte[] pickle(T obj)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        return baos.toByteArray();
    }

    /**
     * Hilfsfunktion
     *
     * @param <T> Generics Parameter
     * @return cl.cast(o);
     * @throws IOException die Excetption
     * @author Keno S.
     * @since Sprint 4
     */
    public static <T extends Serializable> T unpickle(byte[] b, Class<T> cl)
            throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object o = ois.readObject();
        return cl.cast(o);
    }

    /**
     * Hilfsfunktion
     *
     * @param obj das Objekt
     * @param <T> Generics Parameter
     * @return obj.equals(obj2)
     * @throws RuntimeException die Exception
     * @author Keno S.
     * @since Sprint 4
     */
    public static <T extends Serializable> boolean checkSerializableAndDeserializable(T obj, Class<T> cl) {
        try {
            byte[] bytes = pickle(obj);
            T obj2 = unpickle(bytes, cl);
            return obj.equals(obj2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
