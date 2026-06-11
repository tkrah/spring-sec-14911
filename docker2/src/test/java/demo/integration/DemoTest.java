package demo.integration;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class DemoTest {

	private final DataSource dataSource;

	@Autowired
	DemoTest(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Test
	void testDatabase() throws Exception {
		try (Connection conn = dataSource.getConnection()) {
			assertThat(conn.getMetaData().getDatabaseProductName()).isEqualTo("PostgreSQL");
			assertThat(conn.getMetaData().getDatabaseMajorVersion()).isEqualTo(18);
		}
	}
}
