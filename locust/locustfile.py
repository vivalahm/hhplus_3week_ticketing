from locust import HttpUser, TaskSet, task, between
from random import randint

class ConcertTasks(TaskSet):

    def on_start(self):
        self.customer_id = randint(1, 500)

    @task
    def issue_token(self):
        response = self.client.post(
            "/api/token/issue",
            json={"customerId": self.customer_id, "concertId": 2},
            headers={"Content-Type": "application/json"}
        )
        if response.status_code == 200:
            response_data = response.json()
            self.token_value = response_data.get('tokenValue') if response_data.get('status') == 'ACTIVE' else None
            if self.token_value:
                self.get_available_seats()

    def get_available_seats(self):
        if not self.token_value:
            return

        response = self.client.get(
            "/api/41150/available-seats",
            headers={
                "Content-Type": "application/json",
                "Authorization": self.token_value
            }
        )

        if response.status_code == 200:
            seats = response.json().get('seats', [])
            if seats:
                seat_id = seats[0]['id']
                self.reserve_seat(seat_id)

    def reserve_seat(self, seat_id):
        if not self.token_value:
            return

        response = self.client.post(
            "/api/reserve",
            json={
                "concertOptionId": 41150,
                "seatId": seat_id
            },
            headers={
                "Content-Type": "application/json",
                "Authorization": self.token_value
            }
        )

        if response.status_code == 200:
            reservation_id = response.json().get('reservationId')
            if reservation_id:
                self.pay_for_reservation(reservation_id)

    def pay_for_reservation(self, reservation_id):
        if not self.token_value:
            return

        self.client.post(
            "/api/pay",
            json={"reservationId": reservation_id},
            headers={
                "Content-Type": "application/json",
                "Authorization": self.token_value
            }
        )

class ConcertUser(HttpUser):
    tasks = [ConcertTasks]
    wait_time = between(1, 5)