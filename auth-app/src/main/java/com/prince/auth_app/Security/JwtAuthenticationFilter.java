package com.prince.auth_app.Security;

import com.prince.auth_app.helpers.Userhelper;
import com.prince.auth_app.repositories.UserRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header= request.getHeader("Authorization");

        logger.info("Authorizations header:--"+header);

        if(header!=null && header.startsWith("Bearer ")){

            String token= header.substring(7);

            if(!jwtService.isAccessToken(token)){
                filterChain.doFilter(request,response);
                return ;
            }

            try{

                Jws<Claims>parse = jwtService.parse(token);
                Claims payload= parse.getPayload();
                String userId= payload.getSubject();
                UUID userUuiId= Userhelper.parseUuid(userId);

                userRepository.findById(userUuiId)
                        .ifPresent( user-> {

                            if(user.isEnabled()) {

                                List<GrantedAuthority> authorities = user.getRole() == null ? List.of() : user.getRole()
                                        .stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
                                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getEmail(), null, authorities);
                                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                }

                            }
                        }
                );


            }
            catch (ExpiredJwtException e){
                request.setAttribute("error","Token Expired");
                //e.printStackTrace();;
            }
            catch (MalformedJwtException e){
                request.setAttribute("error","Invalid Token");
               // e.printStackTrace();
            }
            catch(JwtException e){
                request.setAttribute("error","Invalid Token");
                //e.printStackTrace();
            } catch (Exception e){
                request.setAttribute("error","Invalid Token");
               // e.printStackTrace();;
            }
        }

        filterChain.doFilter(request,response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return request.getRequestURI().startsWith("/api/v1/auth");
    }
}
