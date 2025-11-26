# Does all SQL queries, returns them to the service layer

class DAO:
    def __init__(self):
        print("DAO init")
    
    def insertExpense(self, userID, amount, description, date):
        print("SQL Insert")

    def selectExpenses(self, userID):
        print("SQL select all expenses according to userID")

    def updateExpense(self, expenseID, field, newVal):
        print("SQL update")
    
    def deleteExpense(self, deleteID):
        print("SQL delete")

    def selectApproval(self, expenseID):
        print("SQL select approval according to expenseID")