import { createFileRoute, useRouter } from "@tanstack/react-router";
import { useAuth } from "#/auth";
import TopNav from "#/components/TopNav";
import HomePageCard from "#/components/HomePageCard";

export const Route = createFileRoute("/")({
  head: () => ({
    meta: [
      {
        title: "Home | Stilton's Store",
      },
    ],
  }),
  component: () => <App />,
});

function App() {
  const router = useRouter();
  const navigate = Route.useNavigate();
  const auth = useAuth();

  const handleLogout = () => {
    auth.logout().then(() => {
      router.invalidate().finally(() => {
        navigate({ to: "/" });
      });
    });
  };

  // Description of G. Stilton
  const founderDescription =
    "G. M. Stilton was born in 1976 and lived his whole life in new Mouse City. A young delinquent moving from city to city, Stilton knew first hand what durable clothes meant. He sold clothes he found to a thrift store to fund his college fashion degree, where he was regarded as one of the most unique and creative designers of the school.";

  return (
    <>
      <TopNav account={auth.user?.accountType} logout={handleLogout} />
      <main className="px-4 pb-8 pt-14">
        <h1 className="hidden">Stilton's Store</h1>
        <div className="flex justify-center">
          <div>
            <img className="w-xl" src="/images/homepage_suit.jpg"></img>
          </div>
          <div className="flex items-center">
            <div className="pl-20 w-[50vw]">
              <p className="text-5xl font-bold">
                Life imitates art, and we are the artists.
              </p>
              <p className="text-3xl pt-5">
                Coming from New Mouse City, Stilton's Store delivers standard
                and funky clothing options at the best quality.
              </p>
            </div>
          </div>
        </div>

        <h2>Who we are</h2>

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

        <h2>Sustainability</h2>

        <div className="flex justify-center">
          <HomePageCard
            title="Green"
            description="We are proud to use recycled ingredients whenever possible."
          ></HomePageCard>
          <HomePageCard
            title="Local"
            description="As opposed to our competitors, we do not outsource our labour. All employees at Stilton's Store are in country and unionized. This is essential to Stilton's philosophy."
          ></HomePageCard>
          <HomePageCard
            title="Low Carbon Equivalent"
            description="The total CO2 equivalent of one of our piece of clothing in its lifecycle is the lowest on the market. We are optimizing energy usage."
          ></HomePageCard>
        </div>

        <h2>Customer Satistfaction</h2>

        <div className="flex justify-center">
          <HomePageCard
            title="Rewards Program"
            description="Our loyalty programs gives discounts to our recurring customers. Loyalty is valued by Stilton, and so should you."
          ></HomePageCard>
          <HomePageCard
            title="Money Back Guarantee"
            description="We do not want you pitching money on something that might not fit you or suit your style. Stilton's store offers 67 days money back guarantee, no questions asked, as long as the product returns intact. Use this policy to flex our clothes, if you want."
          ></HomePageCard>
        </div>
      </main>
    </>
  );
}
