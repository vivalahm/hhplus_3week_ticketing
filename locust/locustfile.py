from locust import task, FastHttpUser

class ConcertTicketingUser(FastHttpUser):
    connection_timeout = 10.0
    network_timeout = 10.0

    @task
    def available_dates(self):
        self.client.get("/api/1/available-dates")