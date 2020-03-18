package at.roedll.pax.jdbc.pre.hook.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

@Component(
    //scope = ServiceScope.SINGLETON,
    immediate = true,
    //service = PreHook.class,
    property = PreHook.KEY_NAME + "=prehooktesthook"
)
public class PreHookTest implements PreHook {

	protected final Logger log = LogManager.getLogger(PreHookTest.class);
	
	public PreHookTest() {
		super();
	}

    @Activate
    public void init() {
		log.error("Starting {}", this.getClass().getSimpleName());
    	
    	//
    }

    @Deactivate
    public void destroy() {
		log.error("Destroying {}", this.getClass().getSimpleName());
   
    	//
    }
    
    @Override
    public void prepare(final DataSource dataSource) throws SQLException {
    	log.error("Called {}.prepare() ...", this.getClass().getSimpleName());
    	
        final String versionQuery = "SELECT \"value\" FROM meta WHERE \"key\" = 'schema_version'";

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement stmt = connection.prepareStatement(versionQuery)) {
                final ResultSet result = stmt.executeQuery();
                final int version = result.getInt("value");

                log.warn("Database schema version is {}", version);
            }
        }

    }
}
