export enum AccountType {
  OWNER = "OWNER",
  EMPLOYEE = "EMPLOYEE",
  CUSTOMER = "CUSTOMER",
  UNKNOWN = "UNKNOWN",
}

export enum ClothingSize {
  XS = "XS",
  S = "S",
  M = "M",
  L = "L",
  XL = "XL",
}

export enum ClothingColour {
  RED = "RED",
  ORANGE = "ORANGE",
  YELLOW = "YELLOW",
  GREEN = "GREEN",
  BLUE = "BLUE",
  PURPLE = "PURPLE",
  PINK = "PINK",
  BLACK = "BLACK",
  GREY = "GREY",
  WHITE = "WHITE",
  BROWN = "BROWN",
}

export enum OrderState {
  PURCHASED = "PURCHASED",
  ASSIGNED = "ASSIGNED",
  PREPARED = "PREPARED",
  DELIVERED = "DELIVERED",
  CANCELLED = "CANCELLED",
}

/* HTTP request objects */
export type RequestObject =
  | AccountRequest
  | ClothingItemRequest
  | ClothingProductRequest
  | CustomerRequest
  | EmployeeRequest
  | OrderItemRequest
  | OrderRequest
  | OrderStatusRequest
  | OwnerRequest
  | ShoppingCartItemRequest;

export interface AccountRequest {
  email: string;
  password: string;
}

export interface ClothingItemRequest {
  size: ClothingSize;
  colour: ClothingColour;
  numInStock: number;
  clothingProductId: number;
}

export interface ClothingProductRequest {
  name: string;
  price: number;
  image: string;
}

export interface CustomerRequest extends AccountRequest {
  address: string;
  numOfLoyaltyPts: number;
}

export interface EmployeeRequest extends CustomerRequest {}

export interface OrderItemRequest {
  clothingItemId: number;
  quantity: number;
  purchasePrice: number;
}

export interface OrderRequest {
  state: OrderState;
  orderDate: Date;
  deliveryDate: Date;
  deliveryAddress: string;
  price: number;
}

export interface OrderStatusRequest {
  state: OrderState;
  employeeId: number;
}

export interface OwnerRequest extends AccountRequest {}

export interface ShoppingCartItemRequest {
  clothingItemId: number;
  quantity: number;
}

/* HTTP response objects */
export interface AccountResponse {
  id: number;
  email: string;
  accountType: AccountType;
}

export interface ClothingItemResponse extends ClothingItemRequest {
  id: number;
}

export interface ClothingProductResponse extends ClothingProductRequest {
  id: number;
  clothingItems: ClothingItemResponse[];
}

export interface CustomerResponse {
  id: number;
  email: string;
  address: string;
  numOfLoyaltyPoints: number;
  shoppingCartItems: ShoppingCartItemResponse[];
  purchasedOrders: OrderResponse[];
}

export interface EmployeeResponse extends CustomerResponse {
  assignedOrders: OrderResponse[];
}

export interface ErrorResponse {
  errors: string[];
}

export interface OrderItemResponse {
  id: number;
  clothingItem: ClothingItemResponse;
  quantity: number;
  purchasePrice: number;
}

export interface OrderResponse extends OrderRequest {
  id: number;
  orderItems: OrderItemResponse[];
}

export interface OwnerResponse {
  id: number;
  email: string;
}

export interface ShoppingCartItemResponse {
  id: number;
  clothingItem: ClothingItemResponse;
  quantity: number;
}
