package com.tealium.remotecommands.braze;

import com.tealium.library.Tealium;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MockBrazeRemoteCommand extends BrazeRemoteCommand {

    public CompletableFuture<Boolean> future = new CompletableFuture<>();
    private List<Validator> validators = new ArrayList<>();

    public MockBrazeRemoteCommand(Tealium.Config config) {
        super(config);
    }

    public MockBrazeRemoteCommand(Tealium.Config config, boolean sessionHandlingEnabled, Set<Class> sessionHandlingBlacklist, boolean registerInAppMessageManager, Set<Class> inAppMessageBlacklist) {
        super(config, sessionHandlingEnabled, sessionHandlingBlacklist, registerInAppMessageManager, inAppMessageBlacklist);
    }

    public MockBrazeRemoteCommand(Tealium.Config config, boolean sessionHandlingEnabled, Set<Class> sessionHandlingBlacklist, boolean registerInAppMessageManager, Set<Class> inAppMessageBlacklist, String commandId, String description) {
        super(config, sessionHandlingEnabled, sessionHandlingBlacklist, registerInAppMessageManager, inAppMessageBlacklist, commandId, description);
    }

    @Override
    protected void onInvoke(Response response) throws Exception {
        super.onInvoke(response);
        if (!validators.isEmpty()) {
            boolean isError = false;
            for (Validator v : validators) {
                if (!v.onValidate(response)) {
                    isError = true;
                    break;
                }
            }

            future.complete(!isError);
        }
    }

    public void setBrazeTrackable(BrazeTrackable brazeTrackable) {
        this.mBraze = brazeTrackable;
    }

    public void addValidator(Validator validator) {
        this.validators.add(validator);
    }

    public interface Validator {
        Boolean onValidate(Response response);
    }
}
