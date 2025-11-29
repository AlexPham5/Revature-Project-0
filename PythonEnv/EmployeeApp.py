import sqlite3
import sys
import logging
from datetime import datetime
from tabulate import tabulate
from utils import util
from DAO import DAO

class EmployeeApp:
    def __init__(self):
        # Logging
        logging.basicConfig(filename="C:\\Users\\alex1\\Revature_work\\Project_0\\pythonlog.log",
                            level=logging.INFO,
                            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')
        logging.info("LOG START")
        self.userID = -1
        self.dbPath = "C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db"
        self.d1 = DAO()

    
    def __del__(self):
        logging.info("LOG END")

    # Employees will submit and manage personal expense reports
    def promptInput(self):
        # Login first, should only let user pass if credentials are valid, otherwise exit program   
        while(1):
            self.login()
            while(1):
                print("\n===== COMMAND OPTIONS =====\n" \
                "1 - Submit a new expense\n" \
                "2 - View all submitted expenses\n" \
                "3 - Edit an existing expense\n" \
                "4 - Delete an existing expense\n" \
                "5 - View approved and denied expenses history\n" \
                "6 - LOGOUT")
                try:
                    userInput = int(input("Please enter a number: "))
                except ValueError:
                    print("Invalid input for command option, value error occurred")
                else:
                    if(userInput == 1):
                        self.submitExpense()
                    elif(userInput == 2):
                        self.viewExpenses()
                    elif(userInput == 3):
                        self.editExpense()
                    elif(userInput == 4):
                        self.deleteExpense()
                    elif(userInput == 5):
                        self.viewApprovalHistory()
                    elif(userInput == 6):
                        break
                    else:
                        print("Invalid command option")

    #- As an employee, I want to log in with my credentials so that I can securely access my 
    #  expense reports.
    # Check the users table for verification
    def login(self):
        while(1):
            try:
                print("\n=== Welcome Employee ===\n" \
                "1 - Enter Credentials\n" \
                "2 - EXIT")
                userInput = int(input("Please enter a number: "))
            except ValueError:
                print("Invalid input for login choice, please enter 1 or 2\n")
                logging.error("Value error for login choice input")
            else:
                if(userInput == 1):
                    try:
                        print("Please enter your employee credentials: ")
                        usernameInput = input("Enter username: ")
                        passwordInput = input("Enter password: ")
                    except ValueError:
                        print("Invalid username/password input, value error occurred")
                        logging.error("Value error for username/password input")
                    else:
                        #check the users table for the valid credentials
                        #check username, password, and role == "Employee", all must be valid
                        print("Checking database for valid credentials")
                        logging.info("Checking users table for valid credentials")
                        try:
                            with sqlite3.connect(self.dbPath) as conn:
                                cursor = conn.cursor()
                                selectCredentials = f"""
                                SELECT * FROM users
                                WHERE username = '{usernameInput}' AND password = '{passwordInput}'
                                """
                                cursor.execute(selectCredentials)
                                credentials = cursor.fetchone()
                                conn.commit()
                                if(credentials != None and credentials[3] == 'Employee'):
                                    #successfully found the entered credentials
                                    print("Successful Login")
                                    logging.info("Successfully found employee credentials")
                                    self.userID = credentials[0]
                                    #print(credentials)
                                    break
                                else:
                                    print("Username or password not found for an employee")
                                    logging.warning("Username not found in database")
                        except sqlite3.Error as error:
                            print("Error occured in login - ", error)  
                            logging.error("SQL error occured in login - ", error)                      
                elif(userInput == 2):
                    logging.info("Exiting login")
                    sys.exit()
                else:
                    logging.warning("Invalid number input for login choice")
                    print("Invalid number input for login options, please enter 1 or 2\n")
        return

    #- As an employee, I want to submit a new expense with details about amount and 
    #  description so that I can request reimbursement or track spending.
    def submitExpense(self):
        #add a value to the expenses table
        try:
                amountInput = float(input("Enter an amount for this expense (at least $1): "))
                if(amountInput <= 0):
                    print("Amount must be greater than $0")
                    logging.warning("Invalid input for amount field in new expense")
                    raise ValueError
                
                descInput = input("Enter a reason for the expense request (at least 3 characters): ")
                if(len(descInput) < 3):
                    print("Description must be at least 3 characters long")
                    logging.warning("Invalid input for description in submit expense")
                    raise ValueError
                
                print("Entering new date for expense: ")
                logging.info("Entering new data for expenses")
                dateInputY = input("Enter a year as 4 digits: ")
                if(len(dateInputY) != 4 or int(dateInputY) > 2025):
                    print("Invalid input for year")
                    logging.warning("Invalid input for year field in new expense")
                    raise ValueError
                dateInputM = input("Enter a month as digits: ")
                if(int(dateInputM) < 1 or int(dateInputM) > 12):
                    print("Invalid input for month")
                    logging.warning("Invalid input for month in new expense")
                    raise ValueError
                dateInputM = f"{int(dateInputM):0{2}d}"
                dateInputD = input("Enter a day as digits: ")
                if(int(dateInputD) < 1 or int(dateInputD) > 31):
                    print("Invalid input for day")
                    logging.warning("Invalid input for day field in new expense")
                    raise ValueError
                dateInputD = f"{int(dateInputD):0{2}d}"
                dateInput = dateInputY + "-" + dateInputM + "-" + dateInputD
                #verify that the date is one that exists and is not in the future
                try:
                    checkDate = datetime.strptime(dateInput, "%Y-%m-%d")
                    now = datetime.now()
                    if(now < checkDate):
                        raise ValueError
                except ValueError:
                    print("Invalid date submission, date does not exists or is in the future")
                    raise ValueError                
        except ValueError:
            print("Invalid input for submit expense\n")       
        else:
            # Call DAO now that all inputs are verified
            self.d1.insertExpense(self.userID, amountInput, descInput, dateInput)
        return

    #- As an employee, I want to view the status of my submitted expenses so that I know 
    #  whether they are pending, approved, or denied.
    def viewExpenses(self):
        # Show all expenses and the corresponding approval's status tied to the user
        # Note, this does not show reviewer, comment, or comment date. That functionality 
        # is for viewApprovalHistory
        self.d1.selectExpenses(self.userID)
        return

    #- As an employee, I want to edit or delete expenses that are still pending so that I 
    #  can correct mistakes before they are reviewed.
    def editExpense(self):
        # ask for expense id, check if its tied to the user and is pending
        # then ask for which field they want to edit (amount, desc, date)
        try:
            self.viewExpenses()
            expenseID = int(input("Please enter the ID of the expense you want to edit: "))
            #check if the expense ID inputted is valid with the DAO
            valid = self.d1.checkExpense(self.userID, expenseID, "pending")
            if(valid == -1):
                print("Error occurred during expense ID verification")
                raise Exception
            elif(valid == 2):
                print("No expense found with specified expense ID for this user")
                raise Exception
            elif(valid == 3):
                print("Expense selected is not pending")
                raise Exception
            elif(valid == 1):
                logging.info("Expense for edit found")
                while(1):
                    print("Expense selected: ")
                    self.d1.printExpense(expenseID)
                    print("Select which field to edit\n" \
                    "1 - Amount\n" \
                    "2 - Description\n" \
                    "3 - Date\n" \
                    "4 - Cancel")
                    try:
                        userInput = int(input("Please enter a number: "))
                        if(userInput == 1):
                            #prompt amount and change it
                            field = "amount"
                            newInput = float(input("Enter a new amount (at least $1): "))
                            if(newInput <= 0):
                                print("Amount value must be at least $1")
                                logging.warning("Invalid input for amount in edit expense")
                                raise ValueError
                        elif(userInput == 2):
                            #prompt description and change it
                            field = "description"
                            newInput = input("Enter a new description (at least 3 characters long): ")
                            if(len(newInput) < 3):
                                print("Description must be at least 3 characters long")
                                logging.warning("Invalid input for description in edit expense")
                                raise ValueError
                        elif(userInput == 3):
                            #prompt date and change it
                            field = "date"
                            print("Entering new date for expense: ")
                            dateInputY = input("Enter a year as 4 digits: ")
                            if(len(dateInputY) != 4 or int(dateInputY) > 2025):
                                print("Invalid input for year")
                                logging.warning("Invalid input for edit expense year")
                                raise ValueError
                            dateInputM = input("Enter a month as digits: ")
                            if(int(dateInputM) < 1 or int(dateInputM) > 12):
                                print("Invalid input for month")
                                logging.warning("Invalid input for edit expense month")
                                raise ValueError
                            dateInputM = f"{int(dateInputM):0{2}d}"
                            dateInputD = input("Enter a day as digits: ")
                            if(int(dateInputD) < 1 or int(dateInputD) > 31):
                                print("Invalid input for day")
                                logging.warning("Invalid input for edit expense day")
                                raise ValueError
                            dateInputD = f"{int(dateInputD):0{2}d}"
                            newInput = dateInputY + "-" + dateInputM + "-" + dateInputD
                            #verify that the date is one that exists and is not in the future
                            try:
                                checkDate = datetime.strptime(newInput, "%Y-%m-%d")
                                now = datetime.now()
                                if(now < checkDate):
                                    raise ValueError
                            except ValueError:
                                print("Invalid date submission, date does not exists or is in the future")
                                raise ValueError
                        elif(userInput == 4):
                            logging.info("Edit expense canceled")
                            break
                        else:
                            raise ValueError
                        # make the change in the db now that you have the inputs
                        self.d1.updateExpense(self.userID, expenseID, field, newInput)
                        break
                    except ValueError:
                        print("Invalid input for field selection")
                        logging.error("Invalid error for field selection in edit expense")
        except ValueError:
            print("Invalid input for edit ID\n")
            logging.error("Invalid input for expenseID in edit expense")
        except Exception as e:
            print("Invalid expense ID")
        return
    
    def deleteExpense(self):
        # ask for expense id, check if its tied to the user and is pending
        # delete expense and the corresponding approval (make sure its pending)
        try:
            self.viewExpenses()
            deleteID = int(input("Please enter the id of the expense you want to delete: "))
            valid = self.d1.checkExpense(self.userID, deleteID, "pending")
            if(valid == -1):
                print("Error occurred during expense ID verification")
                raise Exception
            elif(valid == 2):
                print("No expense found with specified expense ID for this user")
                raise Exception
            elif(valid == 3):
                print("Expense selected is not pending")
                raise Exception
            elif(valid == 1):
                logging.info("Expense for deletion found")
                print("Are you sure you want to delete this expense?")
                self.d1.printExpense(deleteID)

                print("Are you sure you want to delete this expense?")
                confirm = input("Enter YES to confirm: ")
                if(confirm == "YES"):
                    self.d1.deleteExpense(deleteID)
                else:
                    print("Deletion aborted")
                    logging.info("Deletion aborted")
        except ValueError:
            print("Invalid input for deletion ID")
            logging.error("Invalid input for deleteExpense\n")
        except Exception as e:
            print()
        return

    #- As an employee, I want to view a history of all my approved and denied expenses 
    #  so that I can track my financial activity over time.
    def viewApprovalHistory(self):
        # Show all approved and denied approvals tied to the user
        try:
            with sqlite3.connect(self.dbPath) as conn:
                logging.info("Employee attempting to viewing approvals")
                cursor = conn.cursor()
                # grab all the expenses tied to this specific user id
                getExpensesForUser = f"""
                SELECT * FROM expenses WHERE user_id = '{self.userID}'
                """
                cursor.execute(getExpensesForUser)
                expensesList = cursor.fetchall()

                data = [['ID', 'Amount', 'Expense Description', 'Expense Date', 'Status', 'Reviewer ID', 'Comments', 'Review Date']]
                for row in expensesList:
                    cursor.execute(f"SELECT * FROM approvals WHERE expense_id = '{row[0]}' AND status != 'pending'")
                    approval = cursor.fetchone()
                    if(approval != None):
                        amountString = ("$%.2f"%(row[2]))
                        tablerow = [row[0], amountString, row[3], row[4], approval[2], approval[3], approval[4], approval[5]]
                        #print()
                        #print("Expense (ID-%i) with amount $%.2f and description: '%s' made on date: %s" %(row[0], row[2], row[3], row[4]))
                        #print("CURRENT STATUS FOR EXPENSE (ID-%i) IS: %s, reviewed by manager with ID-%i" %(row[0], approval[2], approval[3]))
                        #print("Comments made: %s\nOn date: %s\n" %(approval[4], approval[5]))
                        data.append(tablerow)
                print(tabulate(data, tablefmt="grid"))
                logging.info("Successfuly displayed approvals")

        except sqlite3.Error as error:
            print("SQL Error occured - ", error)
            logging.error("SQL Error occured in view approvals- ", error)
        return

#main
#util.initDB()

# helper function outside of employee app to help test
#helper.addUser("myusername360", "badpassword123", "Employee")
#helper.addUser("otheruser4085", "badpassword123", "Employee")
#helper.addUser("manager123", "password123", "Manager")
#helper.editApproval(4, "approved", 3, "no extra comments", "2025-11-15")
#helper.printTable('users')

    




