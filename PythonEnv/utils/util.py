# Helper functions for debugging the project, not intended 
# functionality for EmployeeApp or ManagerApp
import sqlite3
import logging

#dbPath = '..\\RevatureDatabase.db'
dbPath = 'C:\\Users\\alex1\\Revature_work\\Project_0\\RevatureDatabase.db'

def addUser(username, password, role):
    try:
        conn = sqlite3.connect(dbPath)
        cursor = conn.cursor()

        if(role != "Employee" and role != "Manager"):
             print("New user not added, role must be Employee or Manager")
             return
        cursor.execute("INSERT INTO users (username, password, role) VALUES (?, ?, ?)",
                       (username, password, role))
        
        conn.commit()
        print("Successfully added new user")

    except sqlite3.Error as error:
            print("Error occured - ", error)
    finally:
        if conn:
            conn.close()
            print("DB connection closed")

def printTable(tablename):
    try:
        conn = sqlite3.connect(dbPath)
        cursor = conn.cursor()

        cursor.execute(f"SELECT * FROM {tablename}")
        output = cursor.fetchall()
        for row in output:
            print(row)
        
        conn.commit()
        print("printed table")

    except sqlite3.Error as error:
            print("Error occured - ", error)
    finally:
        if conn:
            conn.close()
            print("DB connection closed")

def editApproval(id, status, reviewer, comment, date):
    try:
        with sqlite3.connect(dbPath) as conn:
            cursor = conn.cursor()
            update = f"""
            UPDATE approvals SET status = '{status}', reviewer = {reviewer}, comment = '{comment}', review_date = '{date}'
            WHERE id = {id}
            """
            cursor.execute(update)
            conn.commit()
            print("Successfully changed approval table")
    except sqlite3.Error as error:
            print("Error occured - ", error)

# access the SQLite database/make on if there isnt one yet
def initDB():
        try:
            conn = sqlite3.connect(dbPath)
            cursor = conn.cursor()
            print("DB Init")
            logging.info("DB Init")

            #Initialize the databases that will be used according to the project's tables
            makeUsersTable = """CREATE TABLE IF NOT EXISTS users(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            username TEXT NOT NULL UNIQUE,
            password TEXT NOT NULL,
            role TEXT) 
            """
            cursor.execute(makeUsersTable)
            print("Created users table")
            logging.info("Created users table")

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
            logging.info("Created expenses table")

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
            logging.info("Created approvals table")

            conn.commit()
            cursor.close()
            logging.info("Database init successful")
        except sqlite3.Error as error:
            print("Error occured - ", error)
            logging.info("Error occured - ", error)
        finally:
            if conn:
                conn.close()
                print("DB connection closed")
                logging.info("DB connection closed")

