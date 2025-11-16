# Helper functions for debugging the project, not intended 
# functionality for EmployeeApp or ManagerApp
import sqlite3

dbPath = '..\\RevatureDatabase.db'

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

