package it.mltk.eebp.utils;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(
            Authentication auth, Object targetDomainObject, Object permission) {
        System.out.println("hasPermission1");
        if(auth == null) {
            System.out.println("auth null");
        } else {
            System.out.println(auth.getName());
        }
        if(targetDomainObject == null) {
            System.out.println("target null");
        } else {
            System.out.println(targetDomainObject);
        }
        if(permission == null) {
            System.out.println("permission null");
        } else {
            System.out.println(permission);
        }
        if ((auth == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

        return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(
            Authentication auth, Serializable targetId, String targetType, Object permission) {
        System.out.println("hasPermission2");
        if ((auth == null) || (targetType == null) || !(permission instanceof String)) {
            return false;
        }
        return hasPrivilege(auth, targetType.toUpperCase(),
                permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
        System.out.println("hasPrivilege: " + targetType + ", " + permission);

        OAuth2Authentication authentication = (OAuth2Authentication) auth;
        LinkedHashMap<String, String> details = (LinkedHashMap<String, String>)authentication.getUserAuthentication().getDetails();
        System.out.println(details.get("email"));
        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
            System.out.println(grantedAuth.getAuthority());
            if (grantedAuth.getAuthority().startsWith(targetType)) {
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
