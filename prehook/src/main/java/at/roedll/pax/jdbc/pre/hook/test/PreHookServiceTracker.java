package at.roedll.pax.jdbc.pre.hook.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ops4j.pax.jdbc.hook.PreHook;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.util.tracker.ServiceTracker;

@Component(
    scope = ServiceScope.SINGLETON,
    immediate = true
)
public class PreHookServiceTracker {

	protected final Logger log = LogManager.getLogger(PreHookServiceTracker.class);
	
	public PreHookServiceTracker() {
		super();
		
		final Bundle bundle = FrameworkUtil.getBundle(PreHookServiceTracker.class);
		if (bundle != null) {
			final BundleContext bundleContext = bundle.getBundleContext();
			if (bundleContext != null) {
				final String phFilter = getPreHookFilter("prehooktesthook");
				
				// Test the ServiceTrackerHelper from PAX-JDBC
				final ServiceTrackerHelper helper = ServiceTrackerHelper.helper(bundleContext);
				final ServiceTracker<?, ?> sthTracker = helper.track(PreHook.class, phFilter, (x, y) -> {
					log.debug("STH Test 1: {}", x);
					return y;
				},
				z -> log.debug("STH Test 2: {}", z));
				
				if (sthTracker.isEmpty()) {
					log.debug("STH Test: No service found this time ({})", phFilter);
				} else {
					log.debug("STH Test: Service found: '{}' = '{}'", phFilter, sthTracker.getService());
				}

				// Test with "local" service tracker
				final ServiceTracker<?, ?> stTracker = getTracker(bundleContext, PreHook.class, phFilter);
	            stTracker.open();
	            if (stTracker.isEmpty()) {
	                log.info("ST Test: No service found this time ({})", phFilter);
	            } else {
					log.debug("ST Test: Service found: '{}' = '{}'", phFilter, stTracker.getService());
				}
			}
		}
	}
	
	private String getPreHookFilter(final String preHookName) {
        if (preHookName != null) {
            return andFilter(eqFilter("objectClass", PreHook.class.getName()),
                    eqFilter(PreHook.KEY_NAME, preHookName));
        }
        return null;
    }

	private String eqFilter(String key, String value) {
        return value != null ? "(" + key + "=" + value + ")" : null;
    }

	private String andFilter(String... filterList) {
        String last = null;
        StringBuilder filter = new StringBuilder("(&");
        int count = 0;
        for (String filterPart : filterList) {
            if (filterPart != null) {
                last = filterPart;
                filter.append(filterPart);
                count++;
            }
        }
        filter.append(")");

        return count > 1 ? filter.toString() : last;
    }
	
	private <S, T> ServiceTracker<S, T> getTracker(
			BundleContext context,
            Class<S> clazz,
            String filter
    ) {
        if (filter != null) {
            ServiceTracker<S, T> tracker = new ServiceTracker<S, T>(context, getOrCreateFilter(context, filter), null) {
                @Override
                public T addingService(ServiceReference<S> reference) {
                    T result = (T) context.getService(reference);
                    log.info("ST Test: Obtained service dependency: '{}' = '{}'", filter, result);
            		return result;
                }
                @Override
                public void removedService(ServiceReference<S> reference, T service) {
                    T result = (T) context.getService(reference);
                	log.info("ST Test: Lost service dependency: '{}' = '{}'", filter, result);
                    context.ungetService(reference);
                }
            };
            tracker.open();
            if (tracker.isEmpty()) {
            	log.info("ST Test: Waiting for service dependency: {}", filter);
            }
            return tracker;
        } else {
            return new ServiceTracker<>(context, clazz, null);
        }
    }

    private Filter getOrCreateFilter(BundleContext context, String filter) {
        try {
            return context.createFilter(filter);
        } catch (InvalidSyntaxException e) {
            throw new RuntimeException("Unable to create filter", e);
        }
    }

    @Activate
    public void init() {
		log.info("Starting {}", this.getClass().getSimpleName());
    	
    	//
    }

    @Deactivate
    public void destroy() {
		log.info("Destroying {}", this.getClass().getSimpleName());
   
    	//
    }

}
