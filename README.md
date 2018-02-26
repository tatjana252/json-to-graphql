# json-to-graphql

Generates GraphQL schema based on JSON data.

It uses JSON data to generate a valid GraphQL schema, 
including custom types, lists and deeply nested children.

Generated GraphQL schema contains all types, input types for all types and mutations, 
optionally it can generate queries.


## Usage

JSON must have top level object that can contain one or more objects.
If JSON respresents one object then its name **must** be supplied.
Supplied name will be name of GraphQL type.


Function `json->object` will generate Clojure map that corresponds to GraphQL schema.
This function can be chained. 
When all objects are added, using `schema` function string GraphQL schema is created, 
which can be saved to textual file.

##
Example
```clojure
(require '[json-to-graphql :refer [json->graphql graphql-schema]])

(def employee "{ \"id\" :       1,
                 \"name\":     \"John\",
                 \"lastname\": \"Doe\",
                 \"age\" :      23}")

(def company "{ \"id\" :       111,
                \"name\":    \"Microsoft\",
                \"websites\": [\"microsoft.com\", \"msn.com\", \"hotmail.com\"],
                \"address\" : {\"street\" : \"11 Times Square\",
                               \"city\" : \"New York\"} }")

(def schema (-> (json->graphql company  "Company"  nil)
                        (json->graphql employee "Employee" {:query [:employee :lastname]})
                         graphql-schema))
                         
                         ;(object-from-json company  nil  nil) won't work, name must be supplied
```
If queries are needed, map with specified fields should be supplied.

Resulting schema:

```
{
    type Employee {
        id: ID
        name: String
         lastname: String
         age: Int
    }
    type Company {
        id: ID
         name: String
        websites: [String]
        address: Address
    }
    type Address {
        street: String
        city: String
    }
    input EmployeeInput {
        id: ID
        name: String
        lastname: String
        age: Int
    }
    input CompanyInput {
        id: ID
        name: String
        websites: [String]
        address: AddressInput
    }
    input AddressInput {
        street: String
        city: String
    }
    type Query {
        employee_by_lastname(lastname:String): Employee
    }
    type Mutation {
        add_Employee(employeeinput: EmployeeInput): Employee
        add_Company(companyinput: CompanyInput): Company
    }
}
```
Same schema will be generated for JSON below, if no name is provided.


```clojure
(def employee-and-company "{\"employee\": {\"id\" : 1,
                                           \"name\": \"John\",
                                           \"lastname\": \"Doe\",
                                           \"age\" : 23},
                            \"company\" : {\"id\" : 111,
                                           \"name\": \"Microsoft\",
                                           \"websites\": [\"microsoft.com\", \"msn.com\", \"hotmail.com\"],
                                           \"address\" : {\"street\" : \"11 Times Square\",
                                                          \"city\" : \"New York\"}}}")
                                                          
(def schema (-> (json->graphql employee-and-company nil  nil)
                         graphql-schema))
                         

```

If name is supplied, new root type will be generated.

```clojure
(def schema (-> (json->graphql employee-and-company "EmployeeandCompany"  nil)
                         graphql-schema))
```
Mutation are generated for root types only.

```
   type EmployeeandCompany {
     employee: Employee
     company: Company
   }
   input EmployeeandCompanyInput {
     employee: EmployeeInput
     company: CompanyInput
   }
   type Mutation {
     add_EmployeeandCompany(employeeandcompanyinput: EmployeeandCompanyInput): EmployeeandCompany
   }
```

It supports deeply nested objects.

```clojure

(def e "{  
           \"b\":[  
              [  
                 {  
                    \"b1\":1,
                    \"c\":[  
                       {  
                          \"c1\":1
                       },
                       {  
                          \"c1\":1
                       }
                    ]
                 }
              ]
           ]
        }")

(def schema (graphql-schema (json->graphql e "a" nil)))
```

Resulting object types: 
```
type A {
 b: [[B]]
}
type B {
 b1: Int
 c: [C]
}
type C {
 c1: Int
}
```

