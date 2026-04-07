import { IoIosLeaf } from "react-icons/io";
import { FaGift, FaHome, FaMoneyBill } from "react-icons/fa";
import { FaRecycle } from "react-icons/fa6";

import { Button } from "@heroui/react";
import { BsArrowRight } from "react-icons/bs";
import { useNavigate } from "@tanstack/react-router";
import HomePageCard from "#/components/HomePageCard";

export default function HomePage() {
  const navigate = useNavigate();
  // Description of G. Stilton
  const founderDescription =
    "G. M. Stilton was born in 1976 and lived his whole life in New York City. A young dreamer moving from city to city, Stilton knew first hand what durable clothes meant. He sold clothes he found to a thrift store to fund his college fashion degree, where he was regarded as one of the most unique and creative designers of the school.";

  return (
    <>
      <div className="relative w-[120vw]">
        <img
          className="z-[-1] absolute -top-14 -left-10 w-screen brightness-[0.35]"
          src="/images/homepage.jpg"
        ></img>
      </div>

      <div className="px-20 mt-20">
        <h1 className="hidden">Stilton's Store</h1>
        <div className="flex flex-col justify-center gap-5 lg:gap-7">
          <p className="text-5xl text-white font-bold">Timeless pieces.</p>
          <p className="text-xl pt-5 mb-5 text-white">
            Casual and elegant, for those who want to stand out.<br></br>
            Here are Stilton's latest works.
          </p>
          <Button
            variant="tertiary"
            onPress={() => navigate({ to: "/products" })}
          >
            Shop now
            <BsArrowRight />
          </Button>
        </div>

        <h2 className="title mt-45">Who we are</h2>

        <div className="flex justify-center">
          <HomePageCard
            title="Our founder"
            description={founderDescription}
          ></HomePageCard>
          <HomePageCard
            title="Our Team"
            description="Our team consists of 7 dedicated workers, with the same vision as Stilton and hoping to persist his ideas in the world."
          ></HomePageCard>
        </div>

        <h2 className="title">Sustainability</h2>

        <div className="flex justify-center">
          <HomePageCard
            title="Green"
            description="We are proud to use recycled materials whenever possible."
            logo={FaRecycle}
          ></HomePageCard>
          <HomePageCard
            title="Local"
            description="As opposed to our competitors, we do not outsource our labour. All employees at Stilton's Store are in country and unionized. This is essential to Stilton's philosophy."
            logo={FaHome}
          ></HomePageCard>
          <HomePageCard
            title="Low Carbon Equivalent"
            description="The total CO2 equivalent of one of our piece of clothing in its lifecycle is the lowest on the market. We are optimizing energy usage."
            logo={IoIosLeaf}
          ></HomePageCard>
        </div>

        <h2 className="title">Customer Satistfaction</h2>

        <div className="flex justify-center">
          <HomePageCard
            title="Rewards Program"
            description="Our loyalty programs gives discounts to our recurring customers. Stilton values both quality and loyalty!"
            logo={FaGift}
          ></HomePageCard>
          <HomePageCard
            title="Money Back Guarantee"
            description="We do not want you pitching money on something that might not fit you or suit your style. Stilton's store offers 30 days money back guarantee, no questions asked, as long as the product returns intact. Use this policy to flex our clothes, if you want."
            logo={FaMoneyBill}
          ></HomePageCard>
        </div>
      </div>
    </>
  );
}
