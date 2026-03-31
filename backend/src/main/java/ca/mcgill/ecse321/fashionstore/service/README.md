# UtilsService Generated Data

Description of generated data from the `/fashionstore/dev/test` endpoint.

```
customer0 = {
    email:              "ada@language.com",
    password:           "19801980",
    address:            "0 Language Avenue",
    numLoyaltyPoints:   0,
    purchasedOrders:    [ order0 ],
    shoppingCartItems:  [ shoppingCartItem0 ]
}

customer1 = {
    email:              "basic@language.com",
    password:           "19641964",
    address:            "1 Language Avenue",
    numLoyaltyPoints:   100,
    purchasedOrders:    [ order1 ],
    shoppingCartItems:  [ shoppingCartItem1, shoppingCartItem2 ]
}

customer2 = {
    email:              "c@language.com",
    password:           "19731973",
    address:            "2 Language Avenue",
    numLoyaltyPoints:   200,
    purchasedOrders:    [ order2 ],
    shoppingCartItems:  [ shoppingCartItem3 ]
}

employee0 = {
    email:              "dart@language.com",
    password:           "20112011",
    address:            "3 Language Avenue",
    numLoyaltyPoints:   300,
    purchasedOrders:    [ order3 ],
    shoppingCartItems:  [ shoppingCartItem4 ],
    assignedOrders:     [ ]
}

employee1 = {
    email:              "erlang@language.com",
    password:           "19861986",
    address:            "4 Language Avenue",
    numLoyaltyPoints:   400,
    purchasedOrders:    [ order4 ],
    shoppingCartItems:  [ ],
    assignedOrders:     [ ]
}

employee2 = {
    email:              "fsharp@language.com",
    password:           "20052005",
    address:            "5 Language Avenue",
    numLoyaltyPoints:   500,
    purchasedOrders:    [ ],
    shoppingCartItems:  [ shoppingCartItem5 ],
    assignedOrders:     [ ]
}

clothingProduct0 = {
    name:               "Go",
    price:              20.0f,
    image:              "",
    items:              [ clothingItem0, clothingItem1 ]
}

clothingProduct1 = {
    name:               "Haskell",
    price:              40.0f,
    image:              "",
    items:              [ clothingItem2, clothingItem3 ]
}

clothingProduct2 = {
    name:               "Jai",
    price:              60.0f,
    image:              "",
    items:              [ clothingItem4, clothingItem5 ]
}

clothingItem0 = {
    size:               Size.XS,
    colour:             Colour.BLACK,
    numInStock:         0,
    clothingProduct:    clothingProduct0,
}

clothingItem1 = {
    size:               Size.S,
    colour:             Colour.BLUE,
    numInStock:         10,
    clothingProduct:    clothingProduct0,
}

clothingItem2 = {
    size:               Size.M,
    colour:             Colour.BROWN,
    numInStock:         20,
    clothingProduct:    clothingProduct1,
}

clothingItem3 = {
    size:               Size.M,
    colour:             Colour.GREEN,
    numInStock:         30,
    clothingProduct:    clothingProduct1,
}

clothingItem4 = {
    size:               Size.L,
    colour:             Colour.PINK,
    numInStock:         40,
    clothingProduct:    clothingProduct2,
}

clothingItem5 = {
    size:               Size.XL,
    colour:             Colour.PURPLE,
    numInStock:         50,
    clothingProduct:    clothingProduct2,
}

order0 = {
    state:              State.PURCHASED,
    price:              20f,
    orderDate:          ORDER_DATE,
    deliveryDate:       DELIVERY_DATE,
    deliveryAddress:    "0 Language Avenue",
    items:              [ orderItem0 ],
    customer:           customer0,
    employee:           null
}

order1 = {
    state:              State.PURCHASED,
    price:              80f,
    orderDate:          ORDER_DATE,
    deliveryDate:       DELIVERY_DATE,
    deliveryAddress:    "1 Language Avenue",
    items:              [ orderItem1, orderItem2 ],
    customer:           customer1,
    employee:           null
}

order2 = {
    state:              State.PURCHASED,
    price:              120f,
    orderDate:          ORDER_DATE,
    deliveryDate:       DELIVERY_DATE,
    deliveryAddress:    "2 Language Avenue",
    items:              [ orderItem3 ],
    customer:           customer2,
    employee:           null
}

order3 = {
    state:              State.PURCHASED,
    price:              100f,
    orderDate:          ORDER_DATE,
    deliveryDate:       DELIVERY_DATE,
    deliveryAddress:    "3 Language Avenue",
    items:              [ orderItem4, orderItem5 ],
    customer:           employee0,
    employee:           null
}

order4 = {
    state:              State.PURCHASED,
    price:              120f,
    orderDate:          ORDER_DATE,
    deliveryDate:       DELIVERY_DATE,
    deliveryAddress:    "4 Language Avenue",
    items:              [ orderItem6 ],
    customer:           employee1,
    employee:           null
}

orderItem0 = {
    quantity:           1,
    purchasePrice:      20f,
    order:              order0,
    clothingItem:       clothingItem0
}

orderItem1 = {
    quantity:           2,
    purchasePrice:      20f,
    order:              order1,
    clothingItem:       clothingItem1
}

orderItem2 = {
    quantity:           1,
    purchasePrice:      40f,
    order:              order1,
    clothingItem:       clothingItem2
}

orderItem3 = {
    quantity:           3,
    purchasePrice:      40f,
    order:              order2,
    clothingItem:       clothingItem3
}

orderItem4 = {
    quantity:           1,
    purchasePrice:      40f,
    order:              order3,
    clothingItem:       clothingItem3
}

orderItem5 = {
    quantity:           1,
    purchasePrice:      60f,
    order:              order3,
    clothingItem:       clothingItem4
}

orderItem6 = {
    quantity:           2,
    purchasePrice:      60f,
    order:              order4,
    clothingItem:       clothingItem5
}


shoppingCartItem0 = {
    quantity:           2,
    customer:           customer0,
    clothingItem:       clothingItem0
}

shoppingCartItem1 = {
    quantity:           4,
    customer:           customer1,
    clothingItem:       clothingItem1
}

shoppingCartItem2 = {
    quantity:           6,
    customer:           customer1,
    clothingItem:       clothingItem2
}

shoppingCartItem3 = {
    quantity:           2,
    customer:           customer2,
    clothingItem:       clothingItem2
}

shoppingCartItem4 = {
    quantity:           4,
    customer:           employee0,
    clothingItem:       clothingItem3
}

shoppingCartItem5 = {
    quantity:           6,
    customer:           employee2,
    clothingItem:       clothingItem4
}
```
