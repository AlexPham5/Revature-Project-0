# Does all SQL queries, returns them to the service layer
import sqlite3
import logging
from tabulate import tabulate


class DAO:
    def __init__(self):
        print("DAO init")
        logging.basicConfig(filename="C:\\Users\\alex1\\Revature_work\\Project_0\\pythonlog.log",
                            level=logging.INFO,
                            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        self.dbPath = "C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db"
        
    
    def insertExpense(self, userID, amount, description, date):
        #print("SQL Insert")
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
        print(f"ALL EXPENSES FOR USERID: {userID}")
        try:
            with sqlite3.connect(self.dbPath) as conn:
                logging.info("Employee viewing existing expenses")
                cursor = conn.cursor()
                # grab all the expenses tied to this specific user id
                getExpensesForUser = f"""
                SELECT * FROM expenses WHERE user_id = '{userID}'
                """
                cursor.execute(getExpensesForUser)
                expensesList = cursor.fetchall()
                
                data = [['ID', 'Amount', 'Description', 'Date Submitted', 'Status']]
                for row in expensesList:
                    cursor.execute(f"SELECT status FROM approvals WHERE expense_id = '{row[0]}'")
                    status = cursor.fetchone()
                    amountString = ("$%.2f"%(row[2]))
                    tablerow = [row[0], amountString, row[3], row[4], status[0]]
                    data.append(tablerow)
                
                print(tabulate(data, tablefmt="grid"))
                logging.info("Successfully showed existing user expenses")
        except sqlite3.Error as error:
            print("Error occured: \n", error)
            logging.error("SQL error occured in view expense")

    def checkExpense(self, userID, expenseID, statusTarget):
        #return 1 if success, 2 if no id found, 3 if id is not equal to status
        #print("Checking expense ID and status")
        try:
            with sqlite3.connect(self.dbPath) as conn:
                logging.info("checking if id is valid")
                cursor = conn.cursor()
                cursor.execute(f"SELECT * FROM expenses WHERE id = '{expenseID}' AND user_id = '{userID}'")
                expenseTBE = cursor.fetchone()
                if(expenseTBE == None):
                    logging.warning("No expense found with specified ID and user ID")
                    return 2
                else:
                    #check that the expense is the target status
                    expenseID = expenseTBE[0]
                    cursor.execute(f"SELECT status FROM approvals WHERE expense_id = {expenseID}")
                    status = cursor.fetchone()[0]
                    if(status != statusTarget):
                        return 3
                    # Expense exists and is valid status
                    return 1
                    
        except sqlite3.Error as error:
            print("SQL Error occured: \n", error)
            logging.error("SQL Error occured in edit")
        except Exception as e:
            print(e)

    def printExpense(self, expenseID):
        #display the single expense given
        try:
            with sqlite3.connect(self.dbPath) as conn:
                logging.info("checking if id is valid")
                cursor = conn.cursor()
                cursor.execute(f"SELECT * FROM expenses WHERE id = '{expenseID}'")
                expenseTBE = cursor.fetchone()

                data = [['ID', 'Amount', 'Description', 'Date Submitted']]
                amountString = ("%.2f" %expenseTBE[2])
                data.append([expenseTBE[0], amountString, expenseTBE[3], expenseTBE[4]])
                print(tabulate(data, tablefmt="grid"))
        except sqlite3.Error as error:
            print("Error occurred in printExpense")
            logging.error("SQL Error for print expense method in DAO")


    def updateExpense(self, userID, expenseID, field, newVal):
        print("SQL update")
        try:
            with sqlite3.connect(self.dbPath) as conn:
                cursor = conn.cursor()
                update =f"""
                UPDATE expenses SET {field} = '{newVal}' WHERE id = '{expenseID}' AND user_id = '{userID}'
                """
                cursor.execute(update)
                conn.commit()
                print("Successfully edited expense")
                logging.info("Successfully edited expense")
        except sqlite3.Error as error:
            print("Expense update failed")
            print("SQL Error occured: \n", error)
            logging.error("SQL Error occured in edit expense")
             
    
    def deleteExpense(self, deleteID):
        print("SQL delete")

    def selectApproval(self, expenseID):
        print("SQL select approval according to expenseID")