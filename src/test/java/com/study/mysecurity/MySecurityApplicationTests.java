package com.study.mysecurity;

import com.study.mysecurity.config.security.SecurityConfig;
import com.study.mysecurity.domain.user.Role;
import com.study.mysecurity.domain.user.User;
import com.study.mysecurity.domain.user.UserDetailsAdapter;
import com.study.mysecurity.domain.user.conroller.UserApiController;
import com.study.mysecurity.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
//@ContextConfiguration(classes = SecurityConfig.class)
@ImportAutoConfiguration(SecurityConfig.class)
@WebMvcTest(controllers = UserApiController.class)
class MySecurityApplicationTests {

	@Autowired
	private WebApplicationContext context;

	@MockBean
	private UserService userService;

	private TestUserDetailsService testUserDetailsService = new TestUserDetailsService();

	private UserDetailsAdapter userDetailsAdapter;

	private MockMvc mockMvc;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();
		userDetailsAdapter = (UserDetailsAdapter) testUserDetailsService.loadUserByUsername(TestUserDetailsService.EMAIL);
	}

	@Test
	void contextLoads() throws Exception {
		mockMvc.perform(
				get("/api/user")
						.with(user(userDetailsAdapter))
		);
	}

	public class TestUserDetailsService implements UserDetailsService {
		public static final String EMAIL ="test@test.com";

		private User getUser() {
			return User.builder()
					.email(EMAIL)
					.password("password")
					.name("test")
					.role(Role.USER)
					.build();
		}

		@Override
		public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
			if (username.equals(EMAIL)) {
				return new UserDetailsAdapter(getUser());
			}
			return null;
		}
	}
}
