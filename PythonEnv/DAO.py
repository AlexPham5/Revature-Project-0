# Does all SQL queries, returns them to the service layer
import sqlite3
import logging


class DAO:
    def __init__(self):
        print("DAO init")
        logging.basicConfig(filename="C:\\Users\\alex1\\Revature_work\\Project_0\\pythonlog.log",
                            level=logging.INFO,
                            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        self.dbPath = "C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db"
        
    
    def insertExpense(self, userID, amount, description, date):
        print("SQL Insert")
        try:
            with sqlite3.connect(self.dbPath) as conn:
                cursor = conn.cursor()

                addExpense =f"""
                INSERT INTO expenses (user_id, amount, description, date)
                VALUES ('{userID}', '{amount}', '{description}', '{date}')
                """
                cursor.execute(addExpense)
                
                getLastRecordID = """
                SELECT id FROM expenses ORDER BY id DESC LIMIT 1
                """
                cursor.execute(getLastRecordID)
                expenseID = cursor.fetchone()[0]

                # Also make a pending approval with only expense_id and status filled
                # Managers in the java app will fill this out when reviewing
                addApproval = f"""
                INSERT INTO approvals (expense_id, status)
                VALUES ('{expenseID}', 'pending')
                """
                cursor.execute(addApproval)
                conn.commit()
        except sqlite3.Error as error:
            print("SQL Error occured: \n", error)
            logging.error("SQL error occured in submit expense")
        else:
            print("Successfully added new expense and approval pending manager review")
            logging.info("Employee successfully submitted new expense")

    def selectExpenses(self, userID):
        print("SQL select all expenses according to userID")

    def updateExpense(self, expenseID, field, newVal):
        print("SQL update")
    
    def deleteExpense(self, deleteID):
        print("SQL delete")

    def selectApproval(self, expenseID):
        print("SQL select approval according to expenseID")