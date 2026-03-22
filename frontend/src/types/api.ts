enum AccountType {
  Owner = "OWNER",
  Employee = "EMPLOYEE",
  Customer = "CUSTOMER",
  Unknown = "UNKNOWN",
}

enum ClothingSize {
  Xsmall = "XS",
  Small = "S",
  Medium = "M",
  Large = "L",
  Xlarge = "XL",
}

enum ClothingColour {
  Red = "RED",
  Orange = "ORANGE",
  Yellow = "YELLOW",
  Green = "GREEN",
  Blue = "BLUE",
  Purple = "PURPLE",
  Pink = "PINK",
  Black = "BLACK",
  Grey = "GREY",
  White = "WHITE",
  Brown = "BROWN",
}

/* HTTP request objects */
interface AccountRequest {
  id: number;
  email: string;
  password: string;
  address: string;
  numOfLoyaltyPoints: number;
}

/* HTTP response objects */
interface AccountResponse {
  id: number;
  email: string;
  accountType: AccountType;
}

interface ClothingItemResponse {
  id: number;
  size: ClothingSize;
  colour: ClothingColour;
  numInStock: number;
  clothingProductId: number;
}

interface ClothingProductResponse {
  id: number;
  name: string;
  price: number;
  image: string;
  clothingItems: ClothingItemResponse[];
}
