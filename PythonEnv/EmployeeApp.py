## User Stories

### Employee App (Python)
#- As an employee, I want to log in with my credentials so that I can securely access my 
#  expense reports.
#- As an employee, I want to submit a new expense with details about amount and 
#  description so that I can request reimbursement or track spending.
#- As an employee, I want to view the status of my submitted expenses so that I know 
#  whether they are pending, approved, or denied.
#- As an employee, I want to edit or delete expenses that are still pending so that I 
#  can correct mistakes before they are reviewed.
#- As an employee, I want to view a history of all my approved and denied expenses 
#  so that I can track my financial activity over time.

# Employee credentials dictionary
#   - employeeCredentials = {'ID':['Username1','Password1','Role']}
# Expenses dictionary (will be with SQLite in actual project)
#   - expenses = {ID:[UserID, Amount, 'Description', Date]}
# History database? Whenever expenses are approved/denied, add them to the history database

import sqlite3
from helpers import helper
import sys

class EmployeeApp:
    def __init__(self):
        # stores userID from login prompt
        self.userID = -1
        self.dbPath = "..\\RevatureDatabase.db"

    # access the SQLite database/make on if there isnt one yet
    def initDB(self):
        try:
            conn = sqlite3.connect(self.dbPath)
            cursor = conn.cursor()
            print("DB Init")

            #Initialize the databases that will be used according to the project's tables
            makeUsersTable = """CREATE TABLE IF NOT EXISTS users(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT) 
            """
            cursor.execute(makeUsersTable)
            print("Created users table")

            #cursor.execute("DROP TABLE IF EXISTS expenses")
            # user_id is a foreign key to the users table's id
            makeExpensesTable = """CREATE TABLE IF NOT EXISTS expenses(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            user_id INTEGER,
            amount REAL NOT NULL,
            description TEXT,
            date TEXT,
            FOREIGN KEY (user_id) REFERENCES users(id))
            """
            cursor.execute(makeExpensesTable)
            print("Created expenses table")

            #cursor.execute("DROP TABLE IF EXISTS approvals")
            # expense_id is a foreign key to the expenses table's id
            makeApprovalsTable = """CREATE TABLE IF NOT EXISTS approvals(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            expense_id INTEGER,
            status TEXT NOT NULL,
            reviewer INTEGER,
            comment TEXT,
            review_date TEXT,
            FOREIGN KEY (expense_id) REFERENCES expenses(id))
            """
            cursor.execute(makeApprovalsTable)
            print("Created approvals table")

            conn.commit()
            cursor.close()
        except sqlite3.Error as error:
            print("Error occured - ", error)
        finally:
            if conn:
                conn.close()
                print("DB connection closed")

    # Employees will submit and manage personal expense reports
    def promptInput(self):
        # Login first, should only let user pass if credentials are valid, otherwise exit program
        while(1):    
            self.login()
            while(1):
                # Then offer command options
                print("COMMAND OPTIONS: \n" \
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
                print("Welcome employee:\n" \
                "1 - Enter Credentials\n" \
                "2 - EXIT")
                userInput = int(input("Please enter a number: "))
            except ValueError:
                print("Invalid input, value error occurred")
            else:
                if(userInput == 1):
                    try:
                        print("Please enter your employee credentials: ")
                        usernameInput = input("Enter username: ")
                        passwordInput = input("Enter password: ")
                    except ValueError:
                        print("Invalid username/password input, value error occurred")
                    else:
                        #check the users table for the valid credentials
                        #check username, password, and role == "Employee", all must be valid
                        print("Checking database for valid credentials")
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
                                    self.userID = credentials[0]
                                    #print(credentials)
                                    break
                                else:
                                    print("Username or password not found for an employee")
                        except sqlite3.Error as error:
                            print("Error occured in login - ", error)                        
                elif(userInput == 2):
                    sys.exit()
                else:
                    print("Invalid number input for login options")
        return

    #- As an employee, I want to submit a new expense with details about amount and 
    #  description so that I can request reimbursement or track spending.
    def submitExpense(self):
        #add a value to the expenses table
        try:
                amountInput = float(input("Enter an amount for this expense: "))
                descInput = input("Enter a reason for the expense request: ")
                print("Entering new date for expense: ")
                dateInputY = input("Enter a year as 4 digits: ")
                if(len(dateInputY) > 4):
                    print("Invalid input for year, must be 4 digits")
                    raise ValueError
                dateInputM = input("Enter a month as 2 digits: ")
                if(len(dateInputM) != 2 or int(dateInputM) < 1 or int(dateInputM) > 12):
                    print("Invalid input for month")
                    raise ValueError
                dateInputD = input("Enter a day as 2 digits: ")
                if(len(dateInputD) != 2 or int(dateInputD) < 1 or int(dateInputD) > 31):
                    print("Invalid input for day")
                    raise ValueError
                dateInput = dateInputY + "-" + dateInputM + "-" + dateInputD
        except ValueError:
            print("Invalid input for submitExpense, ValueError")       
        else:
            #print(amountInput)
            #print(descInput)
            #print(dateInput)
            # actually add the expense to the database
            try:
                with sqlite3.connect(self.dbPath) as conn:
                    cursor = conn.cursor()
                    
                    # Add to the expenses table
                    addExpense =f"""
                    INSERT INTO expenses (user_id, amount, description, date)
                    VALUES ('{self.userID}', '{amountInput}', '{descInput}', '{dateInput}')
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
                print("Error occured - ", error)
            else:
                print("Successfully added new expense and approval pending manager review")
        return

    #- As an employee, I want to view the status of my submitted expenses so that I know 
    #  whether they are pending, approved, or denied.
    def viewExpenses(self):
        # Show all expenses and the corresponding approval's status
        # Note, this does not show reviewer, comment, or comment date. That functionality 
        # is for viewApprovalHistory
        try:
            with sqlite3.connect(self.dbPath) as conn:
                cursor = conn.cursor()
                # grab all the expenses tied to this specific user id
                getExpensesForUser = f"""
                SELECT * FROM expenses WHERE user_id = '{self.userID}'
                """
                cursor.execute(getExpensesForUser)
                expensesList = cursor.fetchall()
                
                for row in expensesList:
                    print("Expense (ID-%i) with amount $%.2f and description: '%s' made on date: %s" %(row[0], row[2], row[3], row[4]))
                    cursor.execute(f"SELECT status FROM approvals WHERE expense_id = '{row[0]}'")
                    status = cursor.fetchone()
                    print("CURRENT STATUS FOR EXPENSE (ID-%i) IS: %s\n" %(row[0], status[0]))
        except sqlite3.Error as error:
            print("Error occured - ", error)
        return

    #- As an employee, I want to edit or delete expenses that are still pending so that I 
    #  can correct mistakes before they are reviewed.
    def editExpense(self):
        # ask for expense id, check if its tied to the user and is pending
        # then ask for which field they want to edit (amount, desc, date)
        return
    def deleteExpense(self):
        # ask for expense id, check if its tied to the user and is pending
        # delete expense and the corresponding approval (make sure its pending)
        return

    #- As an employee, I want to view a history of all my approved and denied expenses 
    #  so that I can track my financial activity over time.
    def viewApprovalHistory(self):
        # Show all approved and denied approvals tied to the user
        return

#main
eApp = EmployeeApp()
eApp.initDB()
eApp.promptInput()

# helper function outside of employee app to just add new users to 
#helper.addUser("myusername360", "badpassword123", "Employee")
#helper.addUser("otheruser4085", "badpassword123", "Employee")
#helper.printTable('users')
    




