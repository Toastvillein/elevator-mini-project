import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElevatorLogger {
	public void elevatorDoorProcess() {
		log.info("문이 열립니다.");
		countDown();
		log.info("문이 닫힙니다.");
	}

	public void elevatorMoveProcess(int currentFloor,int targetFloor) {
		log.info("{}층에서 {}층 으로 이동합니다.",currentFloor,targetFloor);
		countDown();
		log.info("{}층에 도착했습니다.",targetFloor);
	}

	public void countDown(){
		try {
			log.info("3...");
			Thread.sleep(1000);
			log.info("2...");
			Thread.sleep(1000);
			log.info("1...");
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}
}
