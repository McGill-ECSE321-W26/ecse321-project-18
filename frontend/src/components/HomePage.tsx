import { IoIosLeaf } from "react-icons/io";
import { FaGift, FaHome, FaMoneyBill } from "react-icons/fa";
import { FaRecycle } from "react-icons/fa6";

import { Button, Description, Separator } from "@heroui/react";
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
      <h1 className="hidden">Stilton's Store</h1>

      <div
        className="bg-[url('/images/homepage.jpg')] -top-14
        bg-cover w-[99vw] aspect-939/279 bg-slate-600 bg-blend-multiply
      -left-6 right-0 relative overflow-x-hidden flex items-center"
      >
        <div className="pl-30">
          <p className="text-5xl text-white font-bold">Timeless pieces.</p>
          <p className="text-xl pt-5 mb-5 text-white">
            Casual and elegant clothing, for those who want to stand out.
            <br></br>
            Here are Stilton's latest works.
          </p>
          <Button
            variant="tertiary"
            onPress={() => navigate({ to: "/products" })}
          >
            Check our produts
            <BsArrowRight />
          </Button>
        </div>
      </div>

      <div
        className="bg-[url('/images/factory.jpeg')] -top-14
        bg-cover w-[99vw] aspect-2000/900 bg-slate-700 bg-blend-multiply
      -left-6 right-0 relative overflow-x-hidden flex items-center justify-around"
      >
        <div className="w-[30%]">
          <p className="text-white text-xl">
            G. M. Stilton was born in 1976 and lived his whole life in New York
            City. As a kid raised in a modest family, Stilton knew first hand
            what durable clothes meant. He sold clothes he found to a thrift
            store to fund his college fashion degree, where he was regarded as
            one of the most unique and creative designers of the school, able to
            transform cheap fabric into fashion statement pieces.<br></br>
            <br></br>
            We are currently located in 6 countries, but we ship worldwide. Our
            hard-working team at Stilton's hope that every piece you buy will be
            memorable.
          </p>
        </div>

        <h2 className="font-bold text-white title">Who we are</h2>
      </div>

      <div
        className="bg-[url('/images/sustainability.jpg')] -top-14
        bg-cover w-[99vw] aspect-2000/900 bg-olive-800 bg-blend-multiply
      -left-6 right-0 relative overflow-x-hidden flex items-center justify-around"
      >
        <h2 className="font-bold text-white title">Sustainability</h2>
        <div className="w-[40%] h-[70%] flex items-center">
          <Separator className="h-50%" orientation="vertical" />
          <div className="pl-20 text-white">
            <div className="mb-5 flex">
              <div className="flex items-center pr-10">
                <FaRecycle size={50} />
              </div>
              <div>
                <p className="text-3xl font-bold">Green</p>
                <p className="text-2xl">
                  We are proud to use recycled materials whenever possible.
                </p>
              </div>
            </div>

            <div className="mb-5 flex">
              <div className="flex items-center pr-10">
                <FaHome size={50} />
              </div>
              <div>
                <p className="text-3xl font-bold">Local</p>
                <p className="text-2xl">
                  We do not outsource our labour. Everything is local.
                </p>
              </div>
            </div>

            <div className="mb-5 flex">
              <div className="flex items-center pr-10">
                <IoIosLeaf size={50} />
              </div>
              <div>
                <p className="text-3xl font-bold">Low footprint</p>
                <p className="text-2xl">
                  The total CO2 equivalent of one of our piece of clothing in
                  its lifecycle is the lowest on the market. We optimize energy
                  usage.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
