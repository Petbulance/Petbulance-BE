import http from 'k6/http';
import { check, sleep } from 'k6';

// 1. 테스트에 사용할 이미지 파일을 바이너리('b') 모드로 미리 읽어옵니다.
// k6 실행 위치에 'receipt.jpg' 파일이 있어야 합니다.
const binFile = open('./animalReceipt4.jpg', 'b');

export const options = {
    scenarios: {
        contacts: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '10s', target: 200 },  // 처음 10초 동안 50명까지 천천히 증가
                { duration: '10s', target: 300 }, // 다음 20초 동안 100명까지 증가
                { duration: '15s', target: 400 }, // 마지막에 300명 도달
                { duration: '10s', target: 0 },   // 종료
            ],
            gracefulRampDown: '0s',
        },
    },
};

export default function () {
    //3. Multipart 데이터 구성
    const data = {
        // 'image'는 서버에서 받는 @RequestPart 이름과 같아야 합니다.
        image: http.file(binFile, 'receipt.jpg', 'image/jpeg'),
    };
                                                            //receipts/test receipts/mock/blocking
    const res  = http.post('http://host.docker.internal:8080/receipts/test', data);

    // 5. 응답 확인 (성공 여부 체크)
    check(res, {
        'is status 200': (r) => r.status === 200,
    });

    // 너무 빠른 반복을 방지하기 위해 약간의 텀을 줍니다 (선택 사항)
}