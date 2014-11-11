import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;


public class LogWriter {
	
	String logPath = "C:\\BotSimulator\\Logs";
	String startData = null;
	
	public LogWriter (){
		initializeLog();
	}
	public LogWriter (String aux){
		logPath = aux + "\\Logs";
		initializeLog();
	}
	
	/*
	 * this method initialized Log File
	 */
	private void initializeLog() {
		Date data = new Date(System.currentTimeMillis());
		SimpleDateFormat formatarDate = new SimpleDateFormat(
				"yyyy-MM-dd_HH_mm_ss");
		String presentDate = formatarDate.format(data);
		File file = new File(logPath+"\\LOG_" + presentDate + ".txt");

		startData = presentDate;

		try {
			FileWriter fw = new FileWriter(file);
			BufferedWriter log = new BufferedWriter(fw);
			log.write("Inicializacao do Arquivo de LOG - Timestamp "
					+ presentDate);
			log.newLine();
			log.close();
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	/*
	 * Add a log entry to the end of the file
	 */
	public void addLogEntry(String str) {
		System.out.println(str);
		Date data = new Date(System.currentTimeMillis());
		SimpleDateFormat formatarDate = new SimpleDateFormat(
				"yyyy-MM-dd_HH_mm_ss");
		String presentDate = formatarDate.format(data);

		try {
			FileWriter fw = new FileWriter(logPath+"\\LOG_" + startData + ".txt", true);
			BufferedWriter log = new BufferedWriter(fw);
			log.write(presentDate + " : " + str);
			log.newLine();
			log.close();
			fw.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

}
