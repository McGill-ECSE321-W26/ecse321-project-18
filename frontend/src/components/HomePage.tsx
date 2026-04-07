import { IoIosLeaf } from "react-icons/io";
import { FaGift, FaHome, FaMoneyBill } from "react-icons/fa";
import { FaRecycle } from "react-icons/fa6";

import HomePageCard from "#/components/HomePageCard";

export default function HomePage() {
  // Description of G. Stilton
  const founderDescription =
    "G. M. Stilton was born in 1976 and lived his whole life in new Mouse City. A young delinquent moving from city to city, Stilton knew first hand what durable clothes meant. He sold clothes he found to a thrift store to fund his college fashion degree, where he was regarded as one of the most unique and creative designers of the school.";

  return (
    <>
      <h1 className="hidden">Stilton's Store</h1>
      <div className="grid lg:grid-cols-2 justify-center gap-7">
        <div>
          <img className="w-full" src="/images/homepage_suit.jpg"></img>
        </div>
        <div className="flex flex-col justify-center gap-5 lg:gap-7">
          <p className="text-5xl font-bold">
            Life imitates art, and we are the artists.
          </p>
          <p className="text-3xl pt-5">
            Coming from New Mouse City, Stilton's Store delivers standard and
            funky clothing options at the best quality.
          </p>
        </div>
      </div>

      <h2 className="title">Who we are</h2>

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
    </>
  );
}
