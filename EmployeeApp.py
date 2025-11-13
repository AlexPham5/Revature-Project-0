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
#   - expenses = {ID:[Amount, Description, reimbursementStatus]}
#       - ID = string reprsenting a number
#       - Amount = float
#       - Description = string
#       - reimbursementStatus = int, 0 = Pending, 1 = Approved, 2 = Denied
# History database? Whenever expenses are approved/denied, add them to the history database



