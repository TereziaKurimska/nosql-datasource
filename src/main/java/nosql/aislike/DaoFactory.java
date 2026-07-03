package nosql.aislike;

import nosql.crawl.DownloadDao;
import org.springframework.jdbc.core.JdbcTemplate;

import com.mysql.cj.jdbc.MysqlDataSource;

import nosql.crawl.MysqlDownloadDao;

public enum DaoFactory {
	INSTANCE;
	
	private JdbcTemplate jdbcTemplateLocal;
	private JdbcTemplate jdbcTemplateSchool;
	private StudentDao studentDao;
	private DownloadDao downloadDao;

	/** Lokalna MySQL v dockeri (docker-compose.yml v koreni projektu). */
	public synchronized JdbcTemplate getJDBCTemplate2() {
		if (jdbcTemplateLocal == null) {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setServerName("localhost");
			dataSource.setPort(3307);
			dataSource.setDatabaseName("ais-like");
			dataSource.setUser("ais-like-user");
			dataSource.setPassword("iceIceBaby");
			jdbcTemplateLocal = new JdbcTemplate(dataSource);
		}
		return jdbcTemplateLocal;
	}

	/** Skolsky server - dostupny len zo skolskej siete / VPN. */
	public synchronized JdbcTemplate getJDBCTemplate() {
		if (jdbcTemplateSchool == null) {
			MysqlDataSource dataSource = new MysqlDataSource();
			dataSource.setServerName("nosql.gursky.sk");
			dataSource.setDatabaseName("ais-like");
			dataSource.setUser("student");
			dataSource.setPassword("nosql");
			jdbcTemplateSchool = new JdbcTemplate(dataSource);
		}
		return jdbcTemplateSchool;
	}


	public synchronized StudentDao getStudentDao() {
		if (studentDao == null) {
			// pouzivame lokalny docker MySQL (getJDBCTemplate2).
			// Ak sa pripojime na skolsku sietu/VPN, mozeme prepnut na getJDBCTemplate().
			studentDao = new MysqlStudentDao(getJDBCTemplate2());
		}
		return studentDao;
	}

	public synchronized DownloadDao getDownloadDao() {
		if (downloadDao == null) {
			downloadDao = new MysqlDownloadDao(getJDBCTemplate2());
		}
		return downloadDao;
	}
}
