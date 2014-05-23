/*
 * See this link http://www.abuse.ch/?p=3499
 * http://www.cert.pl/PDF/2013-06-p2p-rap_en.pdf
 */

public class Botnet {

	public static void main(String[] args) {

		Bot b = new Bot();
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
