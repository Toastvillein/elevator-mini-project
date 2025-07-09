import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class Elevator {
	public void elevatorProcessor(int currentFloor,int targetFloor) {
		ElevatorLogger logger = new ElevatorLogger();
		logger.elevatorMoveProcess(currentFloor,targetFloor);
		logger.elevatorDoorProcess();
	}

	public boolean isSameDirection(int currentFloor, int targetFloor, Direction currentDirection){
		if(currentDirection.equals(Direction.UP) && targetFloor>= currentFloor){
			return true;
		}
		else if (currentDirection.equals(Direction.DOWN) && targetFloor<=currentFloor){
			return true;
		}
		else {
			log.warn("잘못된 계층 이동 요청입니다.");
			return false;
		}
	}

}
