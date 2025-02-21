package com.company.project.common.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectBasedHeadersDto {

    private String requestId;
    private String customerId;
    private String userId;
    private String userRole;
    private String email;
    private String deviceId;
    private String deviceModel;
    private String appVersion;
    private String originService;
    private String userAgent;
    private String clientIp;
    private String lang;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectBasedHeadersDto that)) {
            return false;
        }
        return Objects.equals(requestId, that.requestId)
                && Objects.equals(customerId, that.customerId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(userRole, that.userRole)
                && Objects.equals(email, that.email)
                && Objects.equals(deviceId, that.deviceId)
                && Objects.equals(deviceModel, that.deviceModel)
                && Objects.equals(appVersion, that.appVersion)
                && Objects.equals(originService, that.originService)
                && Objects.equals(userAgent, that.userAgent)
                && Objects.equals(clientIp, that.clientIp)
                && Objects.equals(lang, that.lang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, customerId, userId, userRole, email, deviceId, deviceModel, appVersion,
                originService, userAgent, clientIp, lang);
    }

}
