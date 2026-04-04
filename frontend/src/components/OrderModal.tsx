import {
  Button,
  Calendar,
  Checkbox,
  DateField,
  DatePicker,
  Description,
  FieldError,
  Form,
  Input,
  Label,
  Modal,
  Surface,
  TextField,
} from "@heroui/react";
import { useEffect, useState } from "react";
import { getLocalTimeZone, today } from "@internationalized/date";

import type { UseNavigateResult } from "@tanstack/react-router";
import type { DateValue } from "@internationalized/date";
import type {
  CustomerRequest,
  CustomerResponse,
  OrderRequest,
} from "#/types/api";
import { OrderState } from "#/types/api";
import { getRequest, postRequest, putRequest } from "#/utils/httpClient";

interface OrderModalProps {
  initialPrice: number;
  isDisabled: boolean;
  navigate: UseNavigateResult<"/cart">;
  customerId: number;
  setIsSubmitting: (isSubmitting: boolean) => void;
}

export const OrderModal = ({
  initialPrice,
  isDisabled,
  navigate,
  customerId,
  setIsSubmitting,
}: OrderModalProps) => {
  const [isDone, setIsDone] = useState<boolean>(false);
  const [address, setAddress] = useState<string>("");
  const [customer, setCustomer] = useState<CustomerResponse | null>(null);
  const [useLoyaltyPoints, setUseLoyaltyPoints] = useState<boolean>(false);
  const [deliveryDate, setDeliveryDate] = useState<DateValue | null>(null);
  const [errors, setErrors] = useState<string[]>([]);

  const currentDay = today(getLocalTimeZone());
  const minDate = currentDay.add({ days: 1 }); // minimum day for delivery is tomorrow
  const isDateInvalid =
    deliveryDate != null && deliveryDate.compare(minDate) < 0;

  useEffect(() => {
    const fetchCustomer = async () => {
      const res: CustomerResponse = await getRequest(
        `/account/customer/${customerId}`,
        false,
      );

      setAddress(res.address);
      setCustomer(res);
    };

    fetchCustomer();
  }, [customerId]);

  if (customer == null) {
    return;
  }

  const handleDone = () => {
    // go to orders page to confirm order if there were no issues
    if (errors.length == 0) navigate({ to: "/orders" });
    setIsSubmitting(false);
    setIsDone(false);
    setErrors([]);
  };

  const handleConfirm = async () => {
    // try to make order request
    if (isDateInvalid || deliveryDate == null || address.length == 0) {
      return; // return. these errors should already be displayed by the fields
    }

    const orderRequest: OrderRequest = {
      state: OrderState.PURCHASED,
      orderDate: currentDay.toDate(getLocalTimeZone()),
      deliveryDate: deliveryDate.toDate(getLocalTimeZone()),
      deliveryAddress: address,
      price: finalPrice,
    };

    const loyaltyRequest: CustomerRequest = {
      numOfLoyaltyPoints:
        customer.numOfLoyaltyPoints -
        (useLoyaltyPoints ? 100 : 0) +
        newLoyaltyPoints,
      email: customer.email,
      password: "dummydummy",
      address: customer.address,
    }; // api call we make will only update the number of loyalty points

    // make API call to create order
    try {
      await postRequest(
        `/account/customer/${customerId}/order`,
        orderRequest,
        false,
      );

      await putRequest(
        `/account/customer/${customerId}/loyalty`,
        loyaltyRequest,
        false,
      );
    } catch (error) {
      if (error instanceof AggregateError) {
        setErrors(error.errors);
      }
    } finally {
      // trigger new modal
      setIsDone(true);
    }
  };

  // calculate loyalty points and final price
  let finalPrice = initialPrice;
  if (useLoyaltyPoints) {
    finalPrice = initialPrice - 10;
  }
  const newLoyaltyPoints = Math.floor(finalPrice); // number of loyalty points earnable in this order

  const isConfirmDisabled =
    isDateInvalid || deliveryDate == null || address.length == 0;

  return (
    <>
      <Modal>
        <Button
          isDisabled={isDisabled}
          onPress={() => setIsSubmitting(true)}
          className="w-full min-w-0"
        >
          Proceed
        </Button>
        <Modal.Backdrop isDismissable={false} isKeyboardDismissDisabled>
          <Modal.Container size={isDone ? "lg" : "cover"}>
            <Modal.Dialog>
              <Modal.Header>
                <Modal.Heading className="text-xl">
                  {!isDone && "Confirm Order"}
                  {isDone &&
                    errors.length == 0 &&
                    "Your order has successfully been placed!"}
                  {isDone && errors.length != 0 && "Your order was not placed."}
                </Modal.Heading>
              </Modal.Header>
              <Modal.Body>
                {isDone &&
                  errors.length == 0 &&
                  "Thank you for shopping at Stilton's Store."}
                {isDone && errors.length != 0 && (
                  <>
                    Unfortunately, there has been an issue:
                    {errors.map((error) => (
                      <p key={error}>{error}</p>
                    ))}
                  </>
                )}
                {!isDone && (
                  <>
                    <p>Your order total is ${initialPrice}.</p>
                    <p>
                      You currently have {customer.numOfLoyaltyPoints} loyalty
                      points in your account.
                    </p>
                    <br />
                    <Checkbox
                      id="loyaltypoints"
                      variant="secondary"
                      isSelected={useLoyaltyPoints}
                      onChange={setUseLoyaltyPoints}
                      isDisabled={customer.numOfLoyaltyPoints < 100}
                    >
                      <Checkbox.Control>
                        <Checkbox.Indicator />
                      </Checkbox.Control>
                      <Checkbox.Content>
                        <Label htmlFor="loyaltypoints">
                          Use my loyalty points
                        </Label>
                        <Description>
                          Redeem 100 loyalty points for a $10 discount on your
                          order!
                        </Description>
                      </Checkbox.Content>
                    </Checkbox>{" "}
                    <br />
                    <p className="text-xl">
                      Your final order total is ${finalPrice}.
                    </p>
                    You will receive {newLoyaltyPoints} loyalty points from this
                    order! <br /> <br />
                    <p>
                      Please proceeed with your order information to complete
                      the purchase. Note that we do not offer same-day delivery.
                    </p>{" "}
                    <br />
                    <div className="flex flex-col items-center gap-4">
                      <Surface variant="default">
                        <Form>
                          <TextField
                            isRequired
                            name="address"
                            type="address"
                            value={address}
                            onChange={setAddress}
                          >
                            <Label>Delivery Address</Label>
                            <Input />
                            <FieldError />
                          </TextField>

                          <DatePicker
                            isRequired
                            className="w-64"
                            isInvalid={isDateInvalid}
                            minValue={minDate}
                            name="date"
                            value={deliveryDate}
                            onChange={setDeliveryDate}
                          >
                            <Label>Delivery Date</Label>
                            <DateField.Group fullWidth>
                              <DateField.Input>
                                {(segment) => (
                                  <DateField.Segment segment={segment} />
                                )}
                              </DateField.Input>
                              <DateField.Suffix>
                                <DatePicker.Trigger>
                                  <DatePicker.TriggerIndicator />
                                </DatePicker.Trigger>
                              </DateField.Suffix>
                            </DateField.Group>
                            <FieldError>Date must be after today.</FieldError>
                            <DatePicker.Popover>
                              <Calendar aria-label="Event date">
                                <Calendar.Header>
                                  <Calendar.YearPickerTrigger>
                                    <Calendar.YearPickerTriggerHeading />
                                    <Calendar.YearPickerTriggerIndicator />
                                  </Calendar.YearPickerTrigger>
                                  <Calendar.NavButton slot="previous" />
                                  <Calendar.NavButton slot="next" />
                                </Calendar.Header>
                                <Calendar.Grid>
                                  <Calendar.GridHeader>
                                    {(day) => (
                                      <Calendar.HeaderCell>
                                        {day}
                                      </Calendar.HeaderCell>
                                    )}
                                  </Calendar.GridHeader>
                                  <Calendar.GridBody>
                                    {(date) => <Calendar.Cell date={date} />}
                                  </Calendar.GridBody>
                                </Calendar.Grid>
                                <Calendar.YearPickerGrid>
                                  <Calendar.YearPickerGridBody>
                                    {({ year }) => (
                                      <Calendar.YearPickerCell year={year} />
                                    )}
                                  </Calendar.YearPickerGridBody>
                                </Calendar.YearPickerGrid>
                              </Calendar>
                            </DatePicker.Popover>
                          </DatePicker>
                        </Form>
                      </Surface>
                    </div>
                  </>
                )}
              </Modal.Body>
              <Modal.Footer>
                {!isDone && (
                  <Button
                    slot="close"
                    variant="secondary"
                    onPress={() => setIsSubmitting(false)}
                  >
                    Cancel
                  </Button>
                )}
                {!isDone && (
                  <Button
                    onPress={handleConfirm}
                    isDisabled={isConfirmDisabled}
                  >
                    Confirm
                  </Button>
                )}
                {isDone && (
                  <Button slot="close" onPress={handleDone}>
                    Done
                  </Button>
                )}
              </Modal.Footer>
            </Modal.Dialog>
          </Modal.Container>
        </Modal.Backdrop>
      </Modal>
    </>
  );
};
