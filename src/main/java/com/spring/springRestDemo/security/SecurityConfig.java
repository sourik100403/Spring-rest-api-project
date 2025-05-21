package com.spring.springRestDemo.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.JOSEException;

import com.nimbusds.jose.jwk.JWKSet;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;





@Configuration
@EnableWebSecurity
public class SecurityConfig {
  
     private RSAKey rsaKey;
    @Bean
    public JWKSource<SecurityContext>jwkSource(){
        rsaKey= Jwks.generateRsa();
        JWKSet jwkSet=new JWKSet(rsaKey);
        return (jwkSelector,SecurityContext)->jwkSelector.select(jwkSet);
        }

        //for paswword encoder
        @Bean
        public static PasswordEncoder passwordEncoder(){
            return new BCryptPasswordEncoder();
        }

    @Bean
    public AuthenticationManager authManager(UserDetailsService userDetailsService){
        var authProvider=new DaoAuthenticationProvider();
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager((authProvider));
    }

     @Bean
    public JwtDecoder jwtDecoder()  throws JOSEException{
        return NimbusJwtDecoder.withPublicKey(rsaKey.toRSAPublicKey()).build();

    }


    @Bean
    JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwks) {
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
             .csrf(csrf -> csrf
                .ignoringRequestMatchers("/db-console/**") // Just for H2 Console
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable()) // Just for H2 Console
            )
            // .headers().frameOptions().sameOrigin()
            .securityMatcher("/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/test").authenticated()
                .requestMatchers("/api/v1/auth/users").hasAuthority("SCOPE_ADMIN")
                .requestMatchers("/api/v1/users/{user_id}/update-authorities").hasAuthority("SCOPE_ADMIN")
                .requestMatchers("/api/v1/auth/profile").authenticated()
                .requestMatchers("/api/v1/auth/profile/delete").authenticated()
                .requestMatchers("/api/v1/auth/profile/update-password").authenticated()
                .requestMatchers("/api/v1/auth/updatepassword").authenticated()
                .requestMatchers("/", 
                "/api/v1/auth/token", 
                "/api/v1/auth/users/add", 
                "/swagger-ui/**", 
                "/v3/api-docs/**",
                "/db-console/**"
                ).permitAll()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> {}) // Enables JWT-based resource server
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // No session
            )
            .httpBasic(Customizer.withDefaults()) // replaces .httpBasic()
            .csrf(csrf -> csrf.disable()); // replaces .csrf().disable()

        return http.build();
    }
}


// package com.spring.springRestDemo.security;

// import java.security.KeyFactory;
// import java.security.interfaces.RSAPrivateKey;
// import java.security.interfaces.RSAPublicKey;
// import java.security.spec.PKCS8EncodedKeySpec;
// import java.security.spec.X509EncodedKeySpec;
// import java.util.Base64;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.core.io.Resource;
// import org.springframework.security.authentication.AuthenticationManager;
// import org.springframework.security.authentication.ProviderManager;
// import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.Customizer;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.http.SessionCreationPolicy;
// import org.springframework.security.core.userdetails.User;
// import org.springframework.security.core.userdetails.UserDetails;
// import org.springframework.security.core.userdetails.UserDetailsService;
// import org.springframework.security.oauth2.jwt.JwtDecoder;
// import org.springframework.security.oauth2.jwt.JwtEncoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
// import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
// import org.springframework.security.provisioning.InMemoryUserDetailsManager;
// import org.springframework.security.web.SecurityFilterChain;

// import com.nimbusds.jose.jwk.RSAKey;
// import com.nimbusds.jose.jwk.JWKSet;
// import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
// import com.nimbusds.jose.jwk.source.JWKSource;
// import com.nimbusds.jose.proc.SecurityContext;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Bean
//     public InMemoryUserDetailsManager users() {
//         UserDetails user = User
//             .withUsername("admin")
//             .password("{noop}password") // {noop} means no encoding
//             .authorities("read")
//             .build();
//         return new InMemoryUserDetailsManager(user);
//     }

//     @Bean
//     public AuthenticationManager authManager(UserDetailsService userDetailsService) {
//         DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//         authProvider.setUserDetailsService(userDetailsService);
//         return new ProviderManager(authProvider);
//     }

//     @Bean
//     public JwtDecoder jwtDecoder() throws Exception {
//         RSAPublicKey publicKey = getPublicKey("certs/public.pem");
//         return NimbusJwtDecoder.withPublicKey(publicKey).build();
//     }

//     @Bean
//     public JwtEncoder jwtEncoder() throws Exception {
//         RSAPublicKey publicKey = getPublicKey("certs/public.pem");
//         RSAPrivateKey privateKey = getPrivateKey("certs/private.pem");

//         RSAKey rsaKey = new RSAKey.Builder(publicKey)
//             .privateKey(privateKey)
//             .build();

//         JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(rsaKey));
//         return new NimbusJwtEncoder(jwks);
//     }

//     private RSAPublicKey getPublicKey(String path) throws Exception {
//         Resource resource = new ClassPathResource(path);
//         String key = new String(resource.getInputStream().readAllBytes())
//             .replace("-----BEGIN PUBLIC KEY-----", "")
//             .replace("-----END PUBLIC KEY-----", "")
//             .replaceAll("\\s+", "");

//         byte[] decoded = Base64.getDecoder().decode(key);
//         X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
//         KeyFactory kf = KeyFactory.getInstance("RSA");
//         return (RSAPublicKey) kf.generatePublic(spec);
//     }

//     private RSAPrivateKey getPrivateKey(String path) throws Exception {
//         Resource resource = new ClassPathResource(path);
//         String key = new String(resource.getInputStream().readAllBytes())
//             .replace("-----BEGIN PRIVATE KEY-----", "")
//             .replace("-----END PRIVATE KEY-----", "")
//             .replaceAll("\\s+", "");

//         byte[] decoded = Base64.getDecoder().decode(key);
//         PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
//         KeyFactory kf = KeyFactory.getInstance("RSA");
//         return (RSAPrivateKey) kf.generatePrivate(spec);
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/test").authenticated()
//                 .requestMatchers("/", "/token", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
//                 .anyRequest().permitAll()
//             )
//             .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
//             .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//             .httpBasic(Customizer.withDefaults())
//             .csrf(csrf -> csrf.disable());

//         return http.build();
//     }
// }
