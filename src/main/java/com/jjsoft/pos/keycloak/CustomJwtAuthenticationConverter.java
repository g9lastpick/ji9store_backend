package com.jjsoft.pos.keycloak;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class CustomJwtAuthenticationConverter  implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

//    @Override
//    public AbstractAuthenticationToken convert(Jwt jwt) {
//        Collection<GrantedAuthority> authorities = defaultConverter.convert(jwt);
//        
//        // Keycloak roles (realm_access > roles) -> Spring 권한으로 추가
//        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
//        if (realmAccess != null && realmAccess.containsKey("roles")) {
//            List<String> roles = (List<String>) realmAccess.get("roles");
//            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())));
//        }
//
//        return new JwtAuthenticationToken(jwt, authorities);
//    }
	@Override
	public AbstractAuthenticationToken convert(Jwt jwt) {
	
	    // 반드시 새 리스트 생성
	    Collection<GrantedAuthority> authorities = new java.util.ArrayList<>();
	
	    // 기본 scope 권한 추가
	    Collection<GrantedAuthority> defaultAuth = defaultConverter.convert(jwt);
	    if (defaultAuth != null) {
	        authorities.addAll(defaultAuth);
	    }
	
	    // Keycloak roles 추가
	    Map<String, Object> realmAccess = jwt.getClaim("realm_access");
	    if (realmAccess != null && realmAccess.containsKey("roles")) {
	        List<String> roles = (List<String>) realmAccess.get("roles");
	
	        roles.forEach(role ->
	            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
	        );
	    }
	
	    return new JwtAuthenticationToken(jwt, authorities);
	}
}
