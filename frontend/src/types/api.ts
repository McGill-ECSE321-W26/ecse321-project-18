export enum AccountType {
  Owner = "OWNER",
  Employee = "EMPLOYEE",
  Customer = "CUSTOMER",
  Unknown = "UNKNOWN",
}

export enum ClothingSize {
  Xsmall = "XS",
  Small = "S",
  Medium = "M",
  Large = "L",
  Xlarge = "XL",
}

export enum ClothingColour {
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

export enum OrderState {
  Purchased = "PURCHASED",
  Assigned = "ASSIGNED",
  Prepared = "PREPARED",
  Delivered = "DELIVERED",
  Cancelled = "CANCELLED",
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
