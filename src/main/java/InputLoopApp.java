import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InputLoopApp {
	public static void main(String[] args) {
		BlockingQueue<OutsideRequest> outsideQueue = new LinkedBlockingQueue<>();
		BlockingQueue<Integer> insideQueue = new LinkedBlockingQueue<>();
		Scanner scanner = new Scanner(System.in);

		// 입력 받는 스레드 (메인 스레드)
		new Thread(() -> {
			System.out.println("애플리케이션 시작 (종료하려면 'exit' 입력)");

			while (true) {
				System.out.println("입력값 예시)OUTSIDE 1 UP,INSIDE 2");
				System.out.print("입력값: ");
				String input = scanner.nextLine();
				String[] parts = input.trim().split("\\s+");

				try {
					if(parts[0].equalsIgnoreCase("OUTSIDE")){
						int floor = Integer.parseInt(parts[1]);
						Direction dir = Direction.valueOf(parts[2].toUpperCase());
						OutsideRequest request = new OutsideRequest(floor,dir);
						outsideQueue.put(request);
						System.out.println("외부 버튼 요청 추가됨: " + floor + "층, 방향: " + dir);
					}
					else if (parts[0].equalsIgnoreCase("INSIDE")){
						int targetFloor = Integer.parseInt(parts[1]);
						insideQueue.put(targetFloor);
						System.out.println("내부 버튼 요청 추가됨: " + targetFloor + "층");
					}
					else {
						System.out.println("잘못된 입력 형식입니다. 예시: OUTSIDE 3 UP / INSIDE 5");
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

				if ("exit".equalsIgnoreCase(input)) {
					System.out.println("애플리케이션 종료");
					System.exit(0); // 전체 종료
				}
			}
		}).start();

		// 큐에서 순차적으로 처리하는 소비자 스레드
		new Thread(() -> {
			int currentFloor = 1;
			Direction currentDirection = null;
			Elevator elevator = new Elevator();
			while (true) {
				try {
					if(!outsideQueue.isEmpty()){
						OutsideRequest request= outsideQueue.take();
						int targetFloor = request.floor();
						Direction dir = request.direction();

						System.out.println("외부 요청 처리 중: " + targetFloor + "층 " + dir);

						elevator.elevatorProcessor(currentFloor,targetFloor);
						currentFloor = targetFloor;
						currentDirection = dir;

						if (outsideQueue.isEmpty() && insideQueue.isEmpty()) {
							currentDirection = null;
							System.out.println("요청 모두 처리 완료 → 방향 초기화됨 (정지 상태)");
						}

						continue;
					}

					if(!insideQueue.isEmpty()){
						int target = insideQueue.peek();
						if(currentDirection == null || elevator.isSameDirection(currentFloor,target,currentDirection)){
							insideQueue.take();
							System.out.println("내부 요청 처리 중: " + target + "층");
							elevator.elevatorProcessor(currentFloor,target);
							currentFloor = target;
							System.out.println("현재 위치: " + currentFloor + "층");

							if (outsideQueue.isEmpty() && insideQueue.isEmpty()) {
								currentDirection = null;
								System.out.println("요청 모두 처리 완료 → 방향 초기화됨 (정지 상태)");
							}

						}
						else {
							System.out.println("방향이 반대라 내부 요청 무시: " + target + "층");
							insideQueue.take();
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}

			}
		}).start();
	}
}
