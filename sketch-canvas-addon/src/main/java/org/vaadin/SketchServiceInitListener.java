package org.vaadin;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import com.vaadin.server.RequestHandler;
import com.vaadin.server.ServiceException;
import com.vaadin.server.ServiceInitEvent;
import com.vaadin.server.SessionDestroyEvent;
import com.vaadin.server.SessionDestroyListener;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinServiceInitListener;
import com.vaadin.server.VaadinSession;

public class SketchServiceInitListener implements VaadinServiceInitListener {

    private static AtomicInteger initCount = new AtomicInteger();
    private static AtomicInteger requestCount = new AtomicInteger();

    /*
     * Called when VaadinService is initialized
     *
     */
    @Override
    public void serviceInit(ServiceInitEvent event) {
        initCount.incrementAndGet();

        event.addRequestHandler(new RequestHandler() {
            @Override
            public boolean handleRequest(VaadinSession session,
                    VaadinRequest request, VaadinResponse response)
                    throws IOException {
                requestCount.incrementAndGet();

                // for each request
                session.addRequestHandler(new RequestHandler() {
                    @Override
                    public boolean handleRequest(VaadinSession vaadinSession,
                            VaadinRequest vaadinRequest,
                            VaadinResponse vaadinResponse) throws IOException {
                        // ...
                        return false;
                    }
                });

                // when the session is initialized
                event.getSource()
                        .addSessionInitListener(new SessionInitListener() {
                            @Override
                            public void sessionInit(SessionInitEvent event)
                                    throws ServiceException {
                                // ...
                            }
                        });

                // sessions is invalidated
                event.getSource().addSessionDestroyListener(
                        new SessionDestroyListener() {
                            @Override
                            public void sessionDestroy(
                                    SessionDestroyEvent event) {
                                // ...

                            }
                        });

                return false;
            }
        });
    }

    public static int getInitCount() {
        return initCount.get();
    }

    public static int getRequestCount() {
        return requestCount.get();
    }
}
