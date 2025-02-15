package org.ballerinalang.twitter;

import io.ballerina.runtime.api.Environment;
import io.ballerina.runtime.api.Future;
import io.ballerina.runtime.api.Module;
import io.ballerina.runtime.api.async.Callback;
import io.ballerina.runtime.api.async.StrandMetadata;
import io.ballerina.runtime.api.creators.ErrorCreator;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.MethodType;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BError;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import static io.ballerina.runtime.api.utils.StringUtils.fromString;

public class NativeHttpToTwitterAdaptor {
    public static final String SERVICE_OBJECT = "TWITTER_SERVICE_OBJECT";

    public static void externInit(BObject adaptor, BObject service) {
        adaptor.addNativeData(SERVICE_OBJECT, service);
    }

    public static Object callOnTweet(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnTweet", "onTweet");
    }

    public static Object callOnReply(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnReply", "onReply");
    }

    public static Object callOnReTweet(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnReTweet", "onReTweet");
    }

    public static Object callOnFollower(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnFollower", "onFollower");
    }

    public static Object callOnFavourite(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnFavourite", "onFavourite");
    }

    public static Object callOnDelete(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnDelete", "onDelete");
    }

    public static Object callOnMention(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnMention", "onMention");
    }

    public static Object callOnQuoteTweet(Environment env, BObject adaptor, BMap<BString, Object> message) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        return invokeRemoteFunction(env, serviceObj, message, "callOnQuoteTweet", "onQuoteTweet");
    }

    public static BArray getServiceMethodNames(BObject adaptor) {
        BObject serviceObj = (BObject) adaptor.getNativeData(SERVICE_OBJECT);
        ArrayList<BString> methodNamesList = new ArrayList<>();
        for (MethodType method : serviceObj.getType().getMethods()) {
            methodNamesList.add(StringUtils.fromString(method.getName()));
        }
        return ValueCreator.createArrayValue(methodNamesList.toArray(BString[]::new));
    }

    private static Object invokeRemoteFunction(Environment env, BObject bHttpService, BMap<BString, Object> message,
                                               String parentFunctionName, String remoteFunctionName) {
        Future balFuture = env.markAsync();
        Module module = ModuleUtils.getModule();
        StrandMetadata metadata = new StrandMetadata(module.getOrg(), module.getName(), module.getVersion(),
                parentFunctionName);
        
        Object[] args = new Object[]{message, true};
        env.getRuntime().invokeMethodAsync(bHttpService, remoteFunctionName, null, metadata, new Callback() {
            @Override
            public void notifySuccess(Object result) {
                balFuture.complete(result);
            }

            @Override
            public void notifyFailure(BError bError) {
                BString errorMessage = fromString("service method invocation failed: " + bError.getErrorMessage());
                BError invocationError = ErrorCreator.createError(errorMessage, bError);
                balFuture.complete(invocationError);
            }
        }, args);
        return null;
    }
}
