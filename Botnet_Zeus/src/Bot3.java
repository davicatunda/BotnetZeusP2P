
public class Bot3 {

	public static void main(String[] args) {

		Botnet b = new Botnet("C:\\BotSimulator3");
		b.log.addLogEntry("Opening connections");
		b.openConnection();
		String nextAction = "updatePeerList";
		while(true){
			System.out.println("-------------------"+nextAction);
			switch(nextAction){
			case "updatePeerList":
				b.updatePeerList();
				break;
			case "DGAMode":
				b.DGAMode();
				break;
			case "configuration":
				b.configuration();
				break;
			case "timer":
				b.timer();
				break;
			case "verificationRound":
				b.verificationRound();
				break;
			}
			nextAction=b.nextAction;
		}
	}

}
