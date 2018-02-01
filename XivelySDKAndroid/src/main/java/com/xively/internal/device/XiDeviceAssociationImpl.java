package com.xively.internal.device;

import com.xively.internal.DependencyInjector;
import com.xively.internal.account.XivelyAccount;
import com.xively.internal.rest.provision.StartAssociationWithCode;
import com.xively.messaging.XivelyDeviceAssociationCallback;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class XiDeviceAssociationImpl {
    private final XivelyAccount account;

    public XiDeviceAssociationImpl(XivelyAccount xivelyEndUserAccount) {
        this.account = xivelyEndUserAccount;
    }

    public void startDeviceAssociation(String associationCode, final XivelyDeviceAssociationCallback callback) {

        DependencyInjector.get().provisionWebServices()
                .associateIoTDevice(associationCode, account.getUserName(), new Callback<StartAssociationWithCode.Response>() {
                    @Override
                    public void onResponse(Call<StartAssociationWithCode.Response> call, Response<StartAssociationWithCode.Response> response) {
                        callback.onAssociationSuccess();
                    }

                    @Override
                    public void onFailure(Call<StartAssociationWithCode.Response> call, Throwable t) {
                        XivelyDeviceAssociationCallback.AssociationError error;
                        error = XivelyDeviceAssociationCallback.AssociationError.ASSOCIATION_ERROR;

                        // TODO
                        switch (1) {
                            case 401:
                                error = XivelyDeviceAssociationCallback.AssociationError.UNAUTHORIZED;
                                break;
                            case 400:
                            case 404:
                                error = XivelyDeviceAssociationCallback.AssociationError.INVALID_CODE;
                                break;
                            case 500:
                                error = XivelyDeviceAssociationCallback.AssociationError.SERVER_INTERNAL_ERROR;
                                break;
                            case 503:
                                error = XivelyDeviceAssociationCallback.AssociationError.SERVICE_UNAVAILABLE;
                                break;
                            default:
                                error = XivelyDeviceAssociationCallback.AssociationError.ASSOCIATION_ERROR;
                                break;
                        }

                        callback.onAssociationFailure(error);
                    }
                });
    }
}
