
# Voting system
import cohorte.vote

# iPOPO Decorators
from pelix.ipopo.decorators import ComponentFactory, Provides, Instantiate

@ComponentFactory()
@Provides(cohorte.vote.SERVICE_VOTE_STORE)
@Instantiate('vote-dummy-store')
class VoteDummyStore(object):
    """
    Dummy vote storage component
    """
    def store_vote(self, vote):
        """
        Store a vote to chart

        :param vote: A VoteContent bean
        """
        pass

