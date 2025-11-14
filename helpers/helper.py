# Helper functions for debugging the project, not intended 
# functionality for EmployeeApp or ManagerApp
import sqlite3

def addUser(username, password, role):
    try:
        conn = sqlite3.connect('RevatureDatabase.db')
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

# STILL NEEDS UPDATING
def printTable(tablename):
    try:
        conn = sqlite3.connect('RevatureDatabase.db')
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
