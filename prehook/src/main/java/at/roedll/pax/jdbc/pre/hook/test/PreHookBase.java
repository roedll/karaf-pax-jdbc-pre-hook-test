package at.roedll.pax.jdbc.pre.hook.test;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

public abstract class PreHookBase {

	protected final Logger log = LogManager.getLogger(this.getClass());

    private final BundleContext bundleContext;

    private final Class<?>[] interfaces;

    private ServiceRegistration<?> serviceRegistration;

    public PreHookBase() {
    	this((Class<?>[]) null);
    }

    public PreHookBase(final Class<?> clazz) {
    	this(new Class[] { clazz });
    }

    public PreHookBase(final Class<?>[] interfaces) {
		log.info("Initializing {}", this.getClass().getSimpleName());
		
		final Bundle bundle = FrameworkUtil.getBundle(this.getClass());
		if (bundle != null) {
			bundleContext = bundle.getBundleContext();
		} else {
			bundleContext = null;
		}
		
		this.interfaces = interfaces;
    }

    /**
	 * Init method called by container.
	 */
	public void init() {
		log.info("Starting {}", this.getClass().getSimpleName());
		
		registerService();
	}

	/**
	 * Destroy method called by container. Shut down gracefully
	 */
	public void destroy() {
		log.info("Stopped {}", this.getClass().getSimpleName());
		
		unregisterService();
	}
	
	/**
	 * Gets the bundleContext
	 */
	protected BundleContext getBundleContext() {
		return this.bundleContext;
	}

	private void registerService() {
		if (bundleContext == null) {
			log.error("Unable to register service. BundleContext is null.");
			return;
		}

		final Dictionary<String, Object> properties = new Hashtable<>();
		
		final List<String> tempInterfaces = new ArrayList<>();

		// Add service interfaces
		if (this.interfaces != null && this.interfaces.length > 0) {
			for (Class<?> item : this.interfaces) {
				if (!tempInterfaces.contains(item.getName())) {
					tempInterfaces.add(item.getName());
				}
			}
		}
		
		serviceRegistration = bundleContext.registerService(tempInterfaces.toArray(new String[tempInterfaces.size()]), this, properties);
	}
	
	private void unregisterService() {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}
	
}
