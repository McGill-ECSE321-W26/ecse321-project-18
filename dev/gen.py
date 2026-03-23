#! /usr/bin/env python3

import random, string, sys
import requests
import psycopg
try:
    import readline
except ImportError:
    pass

PROMPT = "> "
BASE = "http://localhost:8080/fashionstore"
CONN_INFO = "host=localhost port=5432 dbname=fashion_store user=postgres password=fashionstore"

def delete_from_db(table: str, id: int):
    with psycopg.connect(CONN_INFO) as conn:
        with conn.cursor() as cur:
            cur.execute("DELETE FROM %s WHERE id = %s", (table, id,))
            if cur.rowcount > 0:
                print(f"Successfully deleted record with ID: {id}.")
            else:
                print(f"No record found with ID: {id}.")

def delete_all_from_db():
    with psycopg.connect(CONN_INFO) as conn:
        with conn.cursor() as cur:
            # https://stackoverflow.com/questions/13223820/postgresql-delete-all-content
            cur.execute("""
                DO $$
                    DECLARE
                        r RECORD;
                    BEGIN
                        FOR r IN (SELECT tablename FROM pg_tables WHERE schemaname = 'public') LOOP
                            EXECUTE 'TRUNCATE TABLE ' || quote_ident(r.tablename) || ' RESTART IDENTITY CASCADE';
                        END LOOP;
                    END
                $$;
            """)
            print("Deleted all data from database")

def random_string(length: int = 5):
    return ''.join(random.choice(string.ascii_letters) for _ in range(length))

def random_int(length: int = 2) -> str:
    return ''.join(str(random.randint(0, 9)) for _ in range(length))

def random_float(length: int = 2) -> str:
    return f"{random_int(length)}.{random_int(2)}"

def create_product():
    obj = {"name": random_string(), "price": random_float(), "image": random_string()}
    response = requests.post(f"{BASE}/clothingproduct", json=obj)
    return obj, response.status_code, response.json()

def delete_product(id: int):
    obj = {"name": random_string(), "price": random_float(), "image": random_string()}
    response = requests.delete(f"{BASE}/clothingproduct/{id}", json=obj)
    return obj, response.status_code

def create_account_obj():
    return {"email": f"{random_string()}@example.com", "password": f"{random_string(8)}"}

def create_employee():
    obj = create_account_obj()
    response = requests.post(f"{BASE}/account/employee", json=obj)
    return obj, response.status_code, response.json()

def delete_employee(id: int):
    delete_from_db("employee", id)

def create_customer():
    obj = create_account_obj()
    response = requests.post(f"{BASE}/account/customer", json=obj)
    return obj, response.status_code, response.json()

def delete_customer(id: int):
    delete_from_db("customer", id)

state = {}

def create_test(num_employees: int = 1, num_customers: int = 1, num_clothing_products: int = 1):
    for i in range(num_employees):
        print(f"\n--- Create employee {i} ---")
        req, status, resp = create_employee()
        print(f"Request: {req}")
        ids = state.get("employee", [])
        ids.append(resp["id"])
        state["employee"] = ids
        print(f"Status code: {status} | Response: {resp}\n")

    for i in range(num_customers):
        print(f"\n--- Create customer {i} ---")
        req, status, resp = create_customer()
        print(f"Request: {req}")
        ids = state.get("customer", [])
        ids.append(resp["id"])
        state["customer"] = ids
        print(f"Status code: {status} | Response: {resp}\n")

    for i in range(num_clothing_products):
        print(f"\n--- Create product {i} ---")
        req, status, resp = create_product()
        print(f"Request: {req}")
        ids = state.get("product", [])
        ids.append(resp["id"])
        state["product"] = ids
        print(f"Status code: {status} | Response: {resp}\n")

def delete_test():
    for id in range(state.get("employee", [])):
        delete_employee(id)

    for id in range(state.get("customer", [])):
        delete_customer(id)

    for id in range(state.get("product", [])):
        delete_product(id)

def print_help():
    print("""\
Help menu - Run from shell or command-line arguments
\t- > help
\t- > delete
\t- > create employee
\t- > delete employee {employeeId}
\t- > create customer
\t- > delete customer {customerId}
\t- > create product
\t- > delete product {productId}
\t- > create test
\t- > delete test
\t- > create test {num_employees} {num_customers} {num_clothing_products}
\t- > delete test
\t- > delete all
""")

def switch_args(args):
    output = None
    match args[0]:
        case "help":
            print_help()
            return None
        case "create":
            match args[1]:
                case "employee":
                    output = create_employee()
                case "customer":
                    output = create_customer()
                case "product":
                    output = create_product()
                case "test":
                    if len(args) == 2:
                        output = create_test()
                    elif len(args) == 5:
                        output = create_test(int(args[2]), int(args[3]), int(args[4]))
                    else:
                        print_help()
                case _:
                    print_help()
        case "delete":
            match args[1]:
                case "employee":
                    output = delete_employee(int(args[2]))
                case "customer":
                    output = delete_customer(int(args[2]))
                case "product":
                    output = delete_product(int(args[2]))
                case "test":
                    output = delete_test()
                case "all":
                    delete_all_from_db()
                case _:
                    print_help()
        case _:
            print_help()
    return output

def run_shell():
    print_help()
    while True:
        inp = input(PROMPT).strip().split(" ")
        if inp[0] == "quit":
            print("Quitting...")
            exit(0)
        try:
            output = switch_args(inp)
        except BaseException as e:
            print(e)
            continue
        if output is None:
            continue
        if len(output) == 2:
            print(f"Request: {output[0]} | Status code: {output[1]}")
        elif len(output) == 3:
            print(f"Request: {output[0]} | Status code: {output[1]} | Response: {output[2]}")

if __name__ == "__main__":
    if len(sys.argv) == 1:
        run_shell()
        exit(0)
    try:
        output = switch_args(sys.argv[1:])
    except BaseException as e:
        print(e)
        exit(1)
    if output is None:
        exit(0)
    if len(output) == 2:
        print(f"Request: {output[0]} | Status code: {output[1]}")
    elif len(output) == 3:
        print(f"Request: {output[0]} | Status code: {output[1]} | Response: {output[2]}")
