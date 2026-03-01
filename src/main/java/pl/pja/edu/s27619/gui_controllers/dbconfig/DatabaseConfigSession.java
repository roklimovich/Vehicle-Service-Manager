package pl.pja.edu.s27619.gui_controllers.dbconfig;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class DatabaseConfigSession {

    private static final StandardServiceRegistry REGISTRY;
    private static final SessionFactory SESSION_FACTORY;

    static {
        StandardServiceRegistry registryTemp = null;
        SessionFactory sessionFactoryTemp = null;
        try {
            registryTemp = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();
            sessionFactoryTemp = new MetadataSources(registryTemp)
                    .buildMetadata()
                    .buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            if (registryTemp != null) {
                StandardServiceRegistryBuilder.destroy(registryTemp);
            }
        }
        REGISTRY = registryTemp;
        SESSION_FACTORY = sessionFactoryTemp;
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null) {
            SESSION_FACTORY.close();
        }
        if (REGISTRY != null) {
            StandardServiceRegistryBuilder.destroy(REGISTRY);
        }
    }
}
